/*
 * =================================================================================================
 *                             Copyright (C) 2017 Universum Studios
 * =================================================================================================
 *         Licensed under the Apache License, Version 2.0 or later (further "License" only).
 * -------------------------------------------------------------------------------------------------
 * You may use this file only in compliance with the License. More details and copy of this License 
 * you may obtain at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * You can redistribute, modify or publish any part of the code written within this file but as it 
 * is described in the License, the software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES or CONDITIONS OF ANY KIND.
 * 
 * See the License for the specific language governing permissions and limitations under the License.
 * =================================================================================================
 */
package universum.studios.android.support.universi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.support.fragment.manage.FragmentController;
import universum.studios.android.support.fragment.manage.FragmentFactory;
import universum.studios.android.transition.BaseNavigationalTransition;

/**
 * An {@link UniversiContextDelegate} implementation that can be used within context of {@link FragmentActivity}.
 * Activity delegate has additional support for {@link Fragment Fragments} management via {@link FragmentController}.
 * Fragment controller can be accessed via {@link #getFragmentController()} and custom controller can
 * be specified via {@link #setFragmentController(FragmentController)}. Fragment factory that provides
 * fragments to be displayed in a context of a specific UniversiActivity can be specified via
 * {@link #setFragmentFactory(FragmentFactory)}.
 * <p>
 * Navigational transition that can be specified via {@link #setNavigationalTransition(BaseNavigationalTransition)}
 * can be used to finish the associated activity.
 *
 * @author Martin Albedinsky
 * @see UniversiFragmentDelegate
 */
final class UniversiActivityDelegate extends UniversiContextDelegate {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiActivityDelegate";

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Controller that is used to show and hide fragments within the associated activity context.
	 */
	private FragmentController mFragmentController;

	/**
	 * Factory providing fragment instances for the {@link #mFragmentController}.
	 */
	private FragmentFactory mFragmentFactory;

	/**
	 * Navigational transition that can be used to finish the associated activity context.
	 */
	private BaseNavigationalTransition mNavigationalTransition;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiActivityDelegate for the given activity <var>context</var>.
	 *
	 * @see UniversiContextDelegate#UniversiContextDelegate(Context)
	 */
	UniversiActivityDelegate(@NonNull FragmentActivity context) {
		super(context);
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 */
	@NonNull
	@Override
	DialogController instantiateDialogController() {
		return new DialogController((FragmentActivity) mContext);
	}

	/**
	 * Starts a loader with the specified <var>id</var>. If there was already started loader with the
	 * same id before, such a loader will be <b>re-started</b>, otherwise new loader will be <b>initialized</b>.
	 *
	 * @param id        Id of the desired loader to start.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Initialized or re-started loader instance or {@code null} if the specified <var>callbacks</var>
	 * do not create loader for the specified <var>id</var>.
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 */
	@Nullable
	<D> Loader<D> startLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		final LoaderManager manager = ((FragmentActivity) mContext).getSupportLoaderManager();
		if (manager.getLoader(id) == null) return initLoader(id, params, callbacks);
		else return restartLoader(id, params, callbacks);
	}

	/**
	 * Initializes a loader with the specified <var>id</var> for the given <var>callbacks</var>.
	 *
	 * @param id        Id of the desired loader to init.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Initialized loader instance or {@code null} if the specified <var>callbacks</var> do
	 * not create loader for the specified <var>id</var>.
	 * @see #startLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 * @see LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 */
	@Nullable
	<D> Loader<D> initLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		return ((FragmentActivity) mContext).getSupportLoaderManager().initLoader(id, params, callbacks);
	}

	/**
	 * Re-starts a loader with the specified <var>id</var> for the given <var>callbacks</var>.
	 *
	 * @param id        Id of the desired loader to re-start.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Re-started loader instance or {@code null} if the specified <var>callbacks</var> do
	 * not create loader for the specified <var>id</var>.
	 * @see #startLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 * @see LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 */
	@Nullable
	<D> Loader<D> restartLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		return ((FragmentActivity) mContext).getSupportLoaderManager().restartLoader(id, params, callbacks);
	}

	/**
	 * Destroys a loader with the specified <var>id</var>.
	 *
	 * @param id Id of the desired loader to destroy.
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see LoaderManager#destroyLoader(int)
	 */
	void destroyLoader(@IntRange(from = 0) int id) {
		((FragmentActivity) mContext).getSupportLoaderManager().destroyLoader(id);
	}

	/**
	 * Sets a navigational transition that can be used to finish associated activity via
	 * {@link #finishWithNavigationalTransition()}
	 *
	 * @param transition The desired transition. Can be {@code null} to clear the current one.
	 * @see #getNavigationalTransition()
	 */
	void setNavigationalTransition(@Nullable BaseNavigationalTransition transition) {
		this.mNavigationalTransition = transition;
	}

	/**
	 * Returns the navigational transition used to finish associated activity via
	 * {@link #finishWithNavigationalTransition()}.
	 *
	 * @return Attached navigational transition or {@code null} if no transition has been specified.
	 * @see #setNavigationalTransition(BaseNavigationalTransition)
	 */
	@Nullable
	BaseNavigationalTransition getNavigationalTransition() {
		return mNavigationalTransition;
	}

	/**
	 * Finishes the associated activity with navigational transition specified via
	 * {@link #setNavigationalTransition(BaseNavigationalTransition)} (if any).
	 *
	 * @return {@code True} if transition has been started, {@code false} otherwise.
	 */
	boolean finishWithNavigationalTransition() {
		if (mNavigationalTransition != null) {
			mNavigationalTransition.finish((FragmentActivity) mContext);
			return true;
		}
		return false;
	}

	/**
	 * Sets a controller used to manage (show/hide) fragments in context of the associated activity.
	 *
	 * @param controller The desired controller. Can be {@code null} to use the default one.
	 * @see #getFragmentController()
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	void setFragmentController(@Nullable FragmentController controller) {
		this.mFragmentController = controller;
		if (mFragmentFactory != null) {
			this.ensureFragmentController();
			mFragmentController.setFactory(mFragmentFactory);
		}
	}

	/**
	 * Returns the default controller or the one specified via {@link #setFragmentController(FragmentController)}
	 * that is used to manage fragments of the associated activity.
	 *
	 * @return Fragment controller instance ready to show/hide fragment instances.
	 */
	@NonNull
	FragmentController getFragmentController() {
		this.ensureFragmentController();
		return mFragmentController;
	}

	/**
	 * Sets a factory that provides fragment instances for fragment controller of this delegate.
	 *
	 * @param factory The desired factory. Can be {@code null} to clear the current one.
	 * @see #setFragmentController(FragmentController)
	 * @see #getFragmentController()
	 * @see #getFragmentFactory()
	 */
	void setFragmentFactory(@Nullable FragmentFactory factory) {
		this.mFragmentFactory = factory;
		this.ensureFragmentController();
		mFragmentController.setFactory(factory);
	}

	/**
	 * Returns the factory providing fragment instances for fragment controller of this delegate.
	 *
	 * @return Instance of fragment factory or {@code null} if no factory has been specified.
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	@Nullable
	FragmentFactory getFragmentFactory() {
		return mFragmentFactory;
	}

	/**
	 * Ensures that the fragment controller is initialized.
	 */
	private void ensureFragmentController() {
		if (mFragmentController == null) this.mFragmentController = new FragmentController((FragmentActivity) mContext);
	}

	/**
	 * Searches for current fragment displayed in container that is used by {@link FragmentController}
	 * of this delegate to show fragments in the context of the associated activity.
	 *
	 * @return Instance of current fragment found via {@link FragmentController#findCurrentFragment()}
	 * or {@code null} if there is no fragment displayed.
	 */
	@Nullable
	public Fragment findCurrentFragment() {
		return mFragmentController == null ? null : mFragmentController.findCurrentFragment();
	}

	/**
	 * Pops stack with fragments of the associated activity via {@link FragmentManager#popBackStack()}
	 *
	 * @return {@code True} if the stack has ben popped, {@code false} otherwise.
	 */
	public boolean popFragmentsBackStack() {
		final FragmentManager fragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			fragmentManager.popBackStack();
			return true;
		}
		return false;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
