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

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import universum.studios.android.support.dialog.DialogOptions;
import universum.studios.android.support.dialog.manage.DialogController;
import universum.studios.android.support.dialog.manage.DialogFactory;
import universum.studios.android.support.dialog.manage.DialogXmlFactory;
import universum.studios.android.support.fragment.manage.FragmentController;
import universum.studios.android.support.fragment.manage.FragmentFactory;
import universum.studios.android.transition.BaseNavigationalTransition;

/**
 * Unified interface for activities provided by the Universi framework.
 *
 * @author Martin Albedinsky
 */
public interface UniversiActivityContext {

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
	<D> Loader<D> startLoader(int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks);

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
	<D> Loader<D> initLoader(int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks);

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
	<D> Loader<D> restartLoader(int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks);

	/**
	 * Destroys a loader with the specified <var>id</var>.
	 *
	 * @param id Id of the desired loader to destroy.
	 * @see #initLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see #restartLoader(int, Bundle, LoaderManager.LoaderCallbacks)
	 * @see LoaderManager#destroyLoader(int)
	 */
	void destroyLoader(int id);

	/**
	 * Sets a navigational transition that will be used to finish this activity context whenever
	 * its {@link Activity#finishAfterTransition()} is invoked.
	 *
	 * @param transition The desired transition. Can be {@code null} to clear the current one.
	 * @see #getNavigationalTransition()
	 */
	void setNavigationalTransition(@Nullable BaseNavigationalTransition transition);

	/**
	 * Returns the navigational transition that will be used to finish this activity.
	 *
	 * @return Transition or {@code null} if no transition has been specified.
	 * @see #setNavigationalTransition(BaseNavigationalTransition)
	 */
	@Nullable
	BaseNavigationalTransition getNavigationalTransition();

	/**
	 * Sets a controller that should be used to show and hide fragments within context of this activity.
	 *
	 * @param controller The desired controller. Can be {@code null} to use the default one.
	 * @see #getFragmentController()
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	void setFragmentController(@Nullable FragmentController controller);

	/**
	 * Returns the controller that can be used to show and hide fragments within context of this
	 * activity.
	 * <p>
	 * If not specified, instance of {@link FragmentController} is instantiated by default.
	 *
	 * @return The fragment controller of this activity.
	 * @see #setFragmentController(FragmentController)
	 */
	@NonNull
	FragmentController getFragmentController();

	/**
	 * Specifies a factory that provides fragment instances for {@link FragmentController} of his activity.
	 *
	 * @param factory The desired factory. Can be {@code null} to remove the current one.
	 * @see #getFragmentFactory()
	 * @see #setFragmentController(FragmentController)
	 * @see #getFragmentController()
	 */
	void setFragmentFactory(@Nullable FragmentFactory factory);

	/**
	 * Returns the current fragment factory specified for this activity.
	 *
	 * @return Fragment factory or {@code null} if no factory has been specified yet.
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	@Nullable
	FragmentFactory getFragmentFactory();

	/**
	 * Sets a controller that should be used to show and dismiss dialogs within context of this activity.
	 *
	 * @param controller The desired controller. Can be {@code null} to use the default one.
	 * @see #getDialogController()
	 * @see #setFragmentFactory(FragmentFactory)
	 */
	void setDialogController(@Nullable DialogController controller);

	/**
	 * Returns the controller that can be used to show and dismiss dialogs within context of this
	 * activity.
	 * <p>
	 * If not specified, instance of {@link DialogController} is instantiated by default.
	 *
	 * @return The dialog controller of this activity.
	 * @see #setDialogController(DialogController)
	 */
	@NonNull
	DialogController getDialogController();

	/**
	 * Specifies a factory that should provide dialog instances that can be parsed from an Xml file
	 * with the specified <var>xmlDialogsSet</var> for {@link DialogController} of this activity.
	 *
	 * @param xmlDialogsSet Resource id of the desired Xml file containing Xml dialogs that the
	 *                      factory should provide for this activity. May be {@code 0} to remove the
	 *                      current one.
	 * @see #setDialogFactory(DialogFactory)
	 */
	void setDialogXmlFactory(@XmlRes int xmlDialogsSet);

	/**
	 * Specifies a factory that provides dialog instances for {@link DialogController} of this activity.
	 *
	 * @param factory The desired factory. Can be {@code null} to remove the current one.
	 * @see #getDialogFactory()
	 * @see #showDialogWithId(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	void setDialogFactory(@Nullable DialogFactory factory);

	/**
	 * Returns the current dialog factory specified for this activity.
	 *
	 * @return Dialog factory or {@code null} if no factory has been specified yet.
	 * @see #setDialogFactory(DialogFactory)
	 */
	@Nullable
	DialogFactory getDialogFactory();

	/**
	 * Same as {@link #showDialogWithId(int, DialogOptions)} with {@code null} options.
	 */
	boolean showDialogWithId(int dialogId);

	/**
	 * Shows a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to show.
	 * @param options  Options for the dialog.
	 * @return {@code True} if dialog has been shown, {@code false} if this activity is currently
	 * <b>paused</b> or does not have its dialog factory specified.
	 * @see DialogController#showDialog(int, DialogOptions)
	 * @see #setDialogFactory(DialogFactory)
	 * @see #dismissDialogWithId(int)
	 */
	boolean showDialogWithId(@IntRange(from = 0) int dialogId, @Nullable DialogOptions options);

	/**
	 * Dismisses a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if this activity is currently
	 * <b>paused</b> or does not have its dialog factory specified.
	 * @see DialogController#dismissDialog(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	boolean dismissDialogWithId(@IntRange(from = 0) int dialogId);

	/**
	 * Same as {@link #showXmlDialog(int, DialogOptions)} with {@code null} options.
	 */
	boolean showXmlDialog(@XmlRes int resId);

	/**
	 * Like {@link #showDialogWithId(int, DialogOptions)}, but in this case will be used internal
	 * instance of {@link DialogXmlFactory} to create (inflate) the desired dialog instance to be
	 * shown.
	 *
	 * @param resId   Resource id of Xml file containing the desired dialog (its specification) to show.
	 * @param options Options for the dialog.
	 * @return {@code True} if dialog has been successfully inflated and shown, {@code false} if
	 * this activity is currently <b>paused</b> or dialog failed to be inflated.
	 * @see DialogXmlFactory#createDialog(int, DialogOptions)
	 * @see #dismissXmlDialog(int)
	 */
	boolean showXmlDialog(@XmlRes int resId, @Nullable DialogOptions options);

	/**
	 * Dismisses an Xml dialog that has been shown via {@link #showXmlDialog(int, DialogOptions)}.
	 *
	 * @param resId Resource id of Xml file containing the desired dialog (its specification) to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if this activity is currently
	 * <b>paused</b>.
	 * @see #showXmlDialog(int, DialogOptions)
	 */
	boolean dismissXmlDialog(@XmlRes int resId);

	/**
	 * Checks whether the current active network is at this time connected or not.
	 *
	 * @return {@code True} if active network is connected, {@code false} otherwise.
	 * @see ConnectivityManager#getActiveNetworkInfo()
	 * @see NetworkInfo#isConnected()
	 * @see #isNetworkConnected(int)
	 */
	boolean isActiveNetworkConnected();

	/**
	 * Checks whether a network with the specified <var>networkType</var> is at this time connected
	 * or no.
	 *
	 * @param networkType The desired network type to check for connection.
	 * @return {@code True} if the requested network is connected, {@code false} otherwise.
	 * @see ConnectivityManager#getNetworkInfo(int)
	 * @see NetworkInfo#isConnected()
	 */
	boolean isNetworkConnected(int networkType);

	/**
	 * Finishes this activity with navigational transition specified via {@link #setNavigationalTransition(BaseNavigationalTransition)}
	 * (if any).
	 *
	 * @return {@code True} if the transition has been used to finish this activity, {@code false} if
	 * standard framework method (either {@link Activity#finish()} or {@link Activity#finishAfterTransition()}),
	 * depending on the current API level, has been used.
	 */
	boolean finishWithNavigationalTransition();
}
