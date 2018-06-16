/*
 * *************************************************************************************************
 *                                 Copyright 2017 Universum Studios
 * *************************************************************************************************
 *                  Licensed under the Apache License, Version 2.0 (the "License")
 * -------------------------------------------------------------------------------------------------
 * You may not use this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 *
 * See the License for the specific language governing permissions and limitations under the License.
 * *************************************************************************************************
 */
package universum.studios.android.universi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.fragment.manage.FragmentController;
import universum.studios.android.fragment.manage.FragmentFactory;
import universum.studios.android.transition.BaseNavigationalTransition;

/**
 * An {@link UniversiContextDelegate} implementation that can be used within context of {@link Activity}.
 * Activity delegate has additional support for {@link Fragment Fragments} management via {@link FragmentController}.
 * Fragment controller can be accessed via {@link #getFragmentController()} and custom controller can
 * be specified via {@link #setFragmentController(FragmentController)}. Fragment factory that provides
 * fragments to be displayed in a context of a specific UniversiActivity can be specified via
 * {@link #setFragmentFactory(FragmentFactory)}.
 * <p>
 * Navigational transition that can be specified via {@link #setNavigationalTransition(BaseNavigationalTransition)}
 * can be used to finish the associated activity.
 * <p>
 * <b>Note</b> that this class has not been made final on purpose so it may be easily mocked in tests,
 * thought it should not been extended.
 *
 * @author Martin Albedinsky
 * @since 1.0
 *
 * @see UniversiFragmentDelegate
 */
public class UniversiActivityDelegate extends UniversiContextDelegate {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiActivityDelegate";

	/*
	 * Interface ===================================================================================
	 */

	/*
	 * Static members ==============================================================================
	 */

	/*
	 * Members =====================================================================================
	 */

	/**
	 * Controller that is used to show and hide fragments within the associated activity context.
	 */
	private FragmentController fragmentController;

	/**
	 * Factory providing fragment instances for the {@link #fragmentController}.
	 */
	private FragmentFactory fragmentFactory;

	/**
	 * Navigational transition that can be used to finish the associated activity context.
	 */
	private BaseNavigationalTransition navigationalTransition;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiActivityDelegate for the given activity <var>context</var>.
	 *
	 * @see UniversiContextDelegate#UniversiContextDelegate(Context)
	 */
	@VisibleForTesting UniversiActivityDelegate(@NonNull final Activity context) {
		super(context);
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * Creates a new instance of UniversiActivityDelegate for the given <var>activity</var>.
	 *
	 * @param activity The activity context in which will be the new delegate used.
	 * @return Ready to be used delegate.
	 */
	@NonNull public static UniversiActivityDelegate create(@NonNull final Activity activity) {
		return new UniversiActivityDelegate(activity);
	}

	/**
	 */
	@Override @NonNull final DialogController instantiateDialogController() {
		return new DialogController((Activity) context);
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
	 *
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 */
	@Nullable public <D> Loader<D> startLoader(@IntRange(from = 0) final int id, @Nullable final Bundle params, @NonNull final LoaderManager.LoaderCallbacks<D> callbacks) {
		final LoaderManager manager = ((Activity) context).getLoaderManager();
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
	 *
	 * @see #startLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 * @see LoaderManager#initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 */
	@Nullable public <D> Loader<D> initLoader(@IntRange(from = 0) final int id, @Nullable final Bundle params, @NonNull final LoaderManager.LoaderCallbacks<D> callbacks) {
		return ((Activity) context).getLoaderManager().initLoader(id, params, callbacks);
	}

	/**
	 * Re-starts a loader with the specified <var>id</var> for the given <var>callbacks</var>.
	 *
	 * @param id        Id of the desired loader to re-start.
	 * @param params    Params for loader.
	 * @param callbacks Callbacks for loader.
	 * @return Re-started loader instance or {@code null} if the specified <var>callbacks</var> do
	 * not create loader for the specified <var>id</var>.
	 *
	 * @see #startLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #destroyLoader(int)
	 * @see LoaderManager#restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 */
	@Nullable public <D> Loader<D> restartLoader(@IntRange(from = 0) final int id, @Nullable final Bundle params, @NonNull final LoaderManager.LoaderCallbacks<D> callbacks) {
		return ((Activity) context).getLoaderManager().restartLoader(id, params, callbacks);
	}

	/**
	 * Destroys a loader with the specified <var>id</var>.
	 *
	 * @param id Id of the desired loader to destroy.
	 *
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see LoaderManager#destroyLoader(int)
	 */
	public void destroyLoader(@IntRange(from = 0) final int id) {
		((Activity) context).getLoaderManager().destroyLoader(id);
	}

	/**
	 * Sets a navigational transition that can be used to finish associated activity via
	 * {@link #finishWithNavigationalTransition()}.
	 * <p>
	 * If the given transition is not {@code null} this method will also configure incoming transitions
	 * for the associated activity via {@link BaseNavigationalTransition#configureIncomingTransitions(Activity)}.
	 *
	 * @param transition The desired transition. May be {@code null} to clear the current one.
	 *
	 * @see #getNavigationalTransition()
	 */
	public void setNavigationalTransition(@Nullable final BaseNavigationalTransition transition) {
		this.navigationalTransition = transition;
		if (transition != null) {
			transition.configureIncomingTransitions((Activity) context);
		}
	}

	/**
	 * Returns the navigational transition used to finish associated activity via
	 * {@link #finishWithNavigationalTransition()}.
	 *
	 * @return Attached navigational transition or {@code null} if no transition has been specified.
	 *
	 * @see #setNavigationalTransition(BaseNavigationalTransition)
	 */
	@Nullable public BaseNavigationalTransition getNavigationalTransition() {
		return navigationalTransition;
	}

	/**
	 * Finishes the associated activity with navigational transition specified via
	 * {@link #setNavigationalTransition(BaseNavigationalTransition)} (if any).
	 *
	 * @return {@code True} if transition has been started, {@code false} otherwise.
	 */
	public boolean finishWithNavigationalTransition() {
		if (navigationalTransition == null) {
			return false;
		}
		this.navigationalTransition.finish((Activity) context);
		return true;
	}

	/**
	 * Sets a controller used to manage (show/hide) fragments in context of the associated activity.
	 *
	 * @param controller The desired controller. May be {@code null} to use the default one.
	 *
	 * @see #getFragmentController()
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	public void setFragmentController(@Nullable final FragmentController controller) {
		this.fragmentController = controller;
		if (fragmentFactory != null) {
			this.ensureFragmentController();
			fragmentController.setFactory(fragmentFactory);
		}
	}

	/**
	 * Returns the default controller or the one specified via {@link #setFragmentController(FragmentController)}
	 * that is used to manage fragments of the associated activity.
	 *
	 * @return Fragment controller instance ready to show/hide fragment instances.
	 */
	@NonNull public FragmentController getFragmentController() {
		this.ensureFragmentController();
		return fragmentController;
	}

	/**
	 * Sets a factory that provides fragment instances for fragment controller of this delegate.
	 *
	 * @param factory The desired factory. May be {@code null} to clear the current one.
	 *
	 * @see #setFragmentController(FragmentController)
	 * @see #getFragmentController()
	 * @see #getFragmentFactory()
	 */
	public void setFragmentFactory(@Nullable final FragmentFactory factory) {
		this.fragmentFactory = factory;
		this.ensureFragmentController();
		this.fragmentController.setFactory(factory);
	}

	/**
	 * Returns the factory providing fragment instances for fragment controller of this delegate.
	 *
	 * @return Instance of fragment factory or {@code null} if no factory has been specified.
	 *
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	@Nullable public FragmentFactory getFragmentFactory() {
		return fragmentFactory;
	}

	/**
	 * Ensures that the fragment controller is initialized.
	 */
	private void ensureFragmentController() {
		if (fragmentController == null) this.fragmentController = new FragmentController((Activity) context);
	}

	/**
	 * Searches for current fragment displayed in container that is used by {@link FragmentController}
	 * of this delegate to show fragments in the context of the associated activity.
	 *
	 * @return Instance of current fragment found via {@link FragmentController#findCurrentFragment()}
	 * or {@code null} if there is no fragment displayed.
	 */
	@Nullable public Fragment findCurrentFragment() {
		return fragmentController == null ? null : fragmentController.findCurrentFragment();
	}

	/**
	 * Pops stack with fragments of the associated activity via {@link FragmentManager#popBackStack()}
	 *
	 * @return {@code True} if the stack has ben popped, {@code false} otherwise.
	 */
	public boolean popFragmentsBackStack() {
		final FragmentManager fragmentManager = ((Activity) context).getFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			fragmentManager.popBackStack();
			return true;
		}
		return false;
	}

	/*
	 * Inner classes ===============================================================================
	 */
}