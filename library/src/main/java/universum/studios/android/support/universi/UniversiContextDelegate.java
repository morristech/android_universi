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

import android.app.DialogFragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.XmlDialog;
import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogFactory;
import universum.studios.android.dialog.manage.DialogXmlFactory;

/**
 * Context delegate of which implementations are used by {@link UniversiActivity}, {@link UniversiCompatActivity}
 * and {@link UniversiActivity} to provide features in unified 'fashion' regardless of context in
 * which are these features supported.
 * <p>
 * A the current version this context delegate supports features described below:
 *
 * <h3>1) {@link DialogFragment DialogFragments} management</h3>
 * This feature is supported using {@link DialogController}. This controller can be accessed via
 * {@link #getDialogController()} and custom controller can be specified via {@link #setDialogController(DialogController)}.
 * <p>
 * Without any set up this delegate can be used to show any implementation of {@link XmlDialog} via
 * {@link #showXmlDialog(int, DialogOptions)} where for such case will be created internal instance
 * of {@link DialogXmlFactory} used to inflate such dialogs from an Xml file containing a
 * <b>single dialog entry</b>.
 * <p>
 * If the associated context wants to display XmlDialogs from an Xml file containing a
 * <b>set of multiple dialog entries</b> or just simple dialogs instantiated via plain Java code it
 * need to set instance of {@link DialogFactory} that can provide that dialogs
 * via {@link #setDialogFactory(DialogFactory)}. Such dialogs can be than displayed
 * via {@link #showDialogWithId(int, DialogOptions)}.
 * <p>
 * Already showing dialogs can be dismissed via {@link #dismissDialogWithId(int)} or {@link #dismissXmlDialog(int)}.
 * <p>
 * If the associated context is paused (indicated via {@link #setPaused(boolean)}), invoking one of
 * {@code showDialog(...)} methods will be ignored.
 *
 * <h3>2) Connection checking</h3>
 * Check whether there is some network connection established or not can be done via {@link #isActiveNetworkConnected()}
 * or for a specific connection type via {@link #isNetworkConnected(int)} for that matter.
 *
 * @author Martin Albedinsky
 */
abstract class UniversiContextDelegate {

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiContextDelegate";

	/**
	 * Request flag indicating whether the wrapped context requested binding of its data to its view
	 * hierarchy or not.
	 */
	static final int REQUEST_BIND_DATA = 0x00000001;

	/**
	 * Flag indicating whether the wrapped context has its view created or not.
	 */
	private static final int PFLAG_VIEW_CREATED = 0x00000001;

	/**
	 * Flag indicating whether the wrapped context is paused or not.
	 */
	private static final int PFLAG_PAUSED = 0x00000001 << 1;

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
	 * Context that is used to access some application data and services.
	 */
	final Context mContext;

	/**
	 * Set of private flags specified for this delegate.
	 */
	private int mPrivateFlags;

	/**
	 * Set of request flags.
	 */
	private int mRequestFlags;

	/**
	 * Controller that is used to show and dismiss dialogs within context that uses this delegate.
	 */
	private DialogController mDialogController;

	/**
	 * Factory providing dialog instances for the {@link #mDialogController}.
	 */
	private DialogFactory mDialogFactory;

	/**
	 * Dialog factory used to inflate dialog instances presented within a single Xml file.
	 */
	private DialogXmlFactory mDialogXmlFactory;

	/**
	 * Manager used to check for established/available connections.
	 */
	private ConnectivityManager mConnectivityManager;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiContextDelegate.
	 *
	 * @param context Context that is used to access some application data and services.
	 */
	UniversiContextDelegate(@NonNull Context context) {
		this.mContext = context;
	}

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Creates a new instance of UniversiActivityDelegate for the given <var>activity</var>.
	 *
	 * @param activity The activity context in which will be the new delegate used.
	 * @return Ready to use context delegate.
	 */
	@NonNull
	public static UniversiActivityDelegate create(@NonNull FragmentActivity activity) {
		return new UniversiActivityDelegate(activity);
	}

	/**
	 * Creates a new instance of UniversiFragmentDelegate for the given <var>fragment</var>.
	 *
	 * @param fragment The fragment context in which will be the new delegate used.
	 * @return Ready to use context delegate.
	 */
	@NonNull
	public static UniversiFragmentDelegate create(@NonNull Fragment fragment) {
		return new UniversiFragmentDelegate(fragment);
	}

	/**
	 * Sets a controller that should be used to show and dismiss dialogs within context that uses
	 * this delegate.
	 *
	 * @param controller The desired controller. Can be {@code null} to use the default one.
	 * @see #getDialogController()
	 */
	void setDialogController(@Nullable DialogController controller) {
		this.mDialogController = controller;
		if (mDialogFactory != null) {
			this.ensureDialogController();
			mDialogController.setDialogFactory(mDialogFactory);
		}
	}

	/**
	 * Returns the controller that can be used to show and dismiss dialogs within context that uses
	 * this delegate
	 * <p>
	 * If not specified, instance of {@link DialogController} is instantiated by default.
	 *
	 * @return The dialog controller of this delegate.
	 * @see #setDialogController(DialogController)
	 */
	@NonNull
	DialogController getDialogController() {
		this.ensureDialogController();
		return mDialogController;
	}

	/**
	 * Ensures that the dialog controller is initialized.
	 */
	private void ensureDialogController() {
		if (mDialogController == null) this.mDialogController = instantiateDialogController();
	}

	/**
	 * Creates a new instance of DialogController for the context that uses this delegate.
	 *
	 * @return New DialogController instance.
	 */
	@NonNull
	abstract DialogController instantiateDialogController();

	/**
	 * Same as {@link #setDialogFactory(DialogFactory)} where will be passed instance of
	 * {@link DialogXmlFactory} with the specified <var>xmlDialogsSet</var>.
	 *
	 * @param xmlDialogsSet Resource id of the desired Xml file containing Xml dialogs that the
	 *                      factory should provide. May be {@code 0} to remove the current one.
	 */
	void setDialogXmlFactory(@XmlRes int xmlDialogsSet) {
		setDialogFactory(xmlDialogsSet == 0 ? null : new DialogXmlFactory(mContext, xmlDialogsSet));
	}

	/**
	 * Specifies a factory that should provide dialog instances for {@link DialogController} of
	 * this delegate.
	 *
	 * @param factory The desired factory. Can be {@code null} to remove the current one.
	 * @see #getDialogFactory()
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	void setDialogFactory(@Nullable DialogFactory factory) {
		this.mDialogFactory = factory;
		this.ensureDialogController();
		mDialogController.setDialogFactory(factory);
	}

	/**
	 * Returns the current dialog factory specified for this delegate.
	 *
	 * @return Dialog factory or {@code null} if no factory has been specified yet.
	 * @see #setDialogFactory(DialogFactory)
	 */
	@Nullable
	DialogFactory getDialogFactory() {
		return mDialogFactory;
	}

	/**
	 * Shows a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to show.
	 * @param options  Options for the dialog.
	 * @return {@code True} if dialog has been shown, {@code false} if context that uses this delegate
	 * is currently <b>paused</b> or does not have its dialog factory specified.
	 * @see DialogController#showDialog(int, DialogOptions)
	 * @see #setDialogFactory(DialogFactory)
	 * @see #dismissDialogWithId(int)
	 */
	boolean showDialogWithId(@IntRange(from = 0) int dialogId, @Nullable DialogOptions options) {
		if (hasPrivateFlag(PFLAG_PAUSED) || mDialogFactory == null) return false;
		this.ensureDialogController();
		return mDialogController.showDialog(dialogId, options);
	}

	/**
	 * Dismisses a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if context that uses this
	 * delegate is currently <b>paused</b> or does not have its dialog factory specified.
	 * @see DialogController#dismissDialog(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	boolean dismissDialogWithId(@IntRange(from = 0) int dialogId) {
		if (hasPrivateFlag(PFLAG_PAUSED) || mDialogFactory == null) return false;
		this.ensureDialogController();
		return mDialogController.dismissDialog(dialogId);
	}

	/**
	 * Like {@link #showDialogWithId(int, DialogOptions)}, but in this case will be used internal
	 * instance of {@link DialogXmlFactory} to create (inflate) the desired dialog instance to be
	 * shown.
	 *
	 * @param resId   Resource id of Xml file containing the desired dialog (its specification) to show.
	 * @param options Options for the dialog.
	 * @return {@code True} if dialog has been successfully inflated and shown, {@code false} if
	 * context that uses this delegate is currently <b>paused</b> or dialog failed to be inflated.
	 * @see DialogXmlFactory#createDialog(int, DialogOptions)
	 * @see #dismissXmlDialog(int)
	 */
	boolean showXmlDialog(@XmlRes int resId, @Nullable DialogOptions options) {
		if (hasPrivateFlag(PFLAG_PAUSED)) return false;
		final DialogXmlFactory dialogFactory = accessDialogXmlFactory();
		final DialogFragment dialog = dialogFactory.createDialog(resId, options);
		if (dialog != null) {
			final String dialogTag = dialogFactory.createDialogTag(resId);
			this.ensureDialogController();
			return mDialogController.showDialog(dialog, dialogTag);
		}
		return false;
	}

	/**
	 * Dismisses an Xml dialog that has been shown via {@link #showXmlDialog(int, DialogOptions)}.
	 *
	 * @param resId Resource id of Xml file containing the desired dialog (its specification) to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if context that uses this
	 * delegate is currently <b>paused</b>.
	 * @see #showXmlDialog(int, DialogOptions)
	 */
	boolean dismissXmlDialog(@XmlRes int resId) {
		if (hasPrivateFlag(PFLAG_PAUSED)) return false;
		this.ensureDialogController();
		return mDialogController.dismissDialog(accessDialogXmlFactory().createDialogTag(resId));
	}

	/**
	 * Ensures that the dialog Xml factory is initialized and returns it. If {@link #mDialogFactory}
	 * is instance of {@link DialogXmlFactory} than it will be returned.
	 *
	 * @return Instance of DialogXmlFactory that can be used to create instances of Xml dialogs.
	 */
	private DialogXmlFactory accessDialogXmlFactory() {
		if (mDialogFactory instanceof DialogXmlFactory) {
			return (DialogXmlFactory) mDialogFactory;
		}
		if (mDialogXmlFactory == null) {
			mDialogXmlFactory = new DialogXmlFactory(mContext);
		}
		return mDialogXmlFactory;
	}

	/**
	 * Checks whether the current active network is at this time connected or not.
	 *
	 * @return {@code True} if active network is connected, {@code false} otherwise.
	 * @see ConnectivityManager#getActiveNetworkInfo()
	 * @see NetworkInfo#isConnected()
	 * @see #isNetworkConnected(int)
	 */
	boolean isActiveNetworkConnected() {
		this.ensureConnectivityManager();
		final NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	/**
	 * Checks whether a network with the specified <var>networkType</var> is at this time connected
	 * or no.
	 *
	 * @param networkType The desired network type to check for connection.
	 * @return {@code True} if the requested network is connected, {@code false} otherwise.
	 * @see ConnectivityManager#getNetworkInfo(int)
	 * @see NetworkInfo#isConnected()
	 */
	boolean isNetworkConnected(int networkType) {
		this.ensureConnectivityManager();
		final NetworkInfo info = mConnectivityManager.getNetworkInfo(networkType);
		return info != null && info.isConnected();
	}

	/**
	 * Ensures that the connectivity manager is initialized.
	 */
	private void ensureConnectivityManager() {
		if (mConnectivityManager == null)
			this.mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	}

	/**
	 * Sets a boolean flag indicating whether a view hierarchy of the related context is created or not.
	 *
	 * @param created {@code True} if view hierarchy is created, {@code false} otherwise.
	 * @see #isViewCreated()
	 */
	void setViewCreated(boolean created) {
		this.updatePrivateFlags(PFLAG_VIEW_CREATED, created);
	}

	/**
	 * Returns the boolean flag indicating whether view hierarchy of the related context is created
	 * or not.
	 *
	 * @return {@code True} if view is created, {@code false} otherwise.
	 * @see #setViewCreated(boolean)
	 */
	boolean isViewCreated() {
		return hasPrivateFlag(PFLAG_VIEW_CREATED);
	}

	/**
	 * Sets a boolean flag indicating whether the related context has been paused or not.
	 *
	 * @param paused {@code True} if context is paused, {@code false} otherwise.
	 */
	void setPaused(boolean paused) {
		this.updatePrivateFlags(PFLAG_PAUSED, paused);
	}

	/**
	 * Returns the boolean flag indicating whether the related context is paused or not.
	 *
	 * @return {@code True} if context is paused, {@code false} otherwise.
	 */
	boolean isPaused() {
		return hasPrivateFlag(PFLAG_PAUSED);
	}

	/**
	 * Adds a request with the specified <var>request</var> flag into the registered ones.
	 *
	 * @param request The request flag that should be registered.
	 * @see #isRequestRegistered(int)
	 * @see #unregisterRequest(int)
	 */
	void registerRequest(int request) {
		this.mRequestFlags |= request;
	}

	/**
	 * Removes request with the specified <var>request</var> flag from the registered ones.
	 *
	 * @param request The request flag that should be unregistered.
	 * @see #registerRequest(int)
	 * @see #isRequestRegistered(int)
	 */
	void unregisterRequest(int request) {
		this.mRequestFlags &= ~request;
	}

	/**
	 * Checks whether a request with the specified <var>request</var> flag is registered or not.
	 *
	 * @param request The request flag for which to check if it is registered.
	 * @return {@code True} if request is registered, {@code false} otherwise.
	 * @see #registerRequest(int)
	 * @see #unregisterRequest(int)
	 */
	boolean isRequestRegistered(int request) {
		return (mRequestFlags & request) != 0;
	}

	/**
	 * Updates the current private flags.
	 *
	 * @param flag Value of the desired flag to add/remove to/from the current private flags.
	 * @param add  Boolean flag indicating whether to add or remove the specified <var>flag</var>.
	 */
	@SuppressWarnings("unused")
	private void updatePrivateFlags(int flag, boolean add) {
		if (add) this.mPrivateFlags |= flag;
		else this.mPrivateFlags &= ~flag;
	}

	/**
	 * Returns a boolean flag indicating whether the specified <var>flag</var> is contained within
	 * the current private flags or not.
	 *
	 * @param flag Value of the flag to check.
	 * @return {@code True} if the requested flag is contained, {@code false} otherwise.
	 */
	@SuppressWarnings("unused")
	private boolean hasPrivateFlag(int flag) {
		return (mPrivateFlags & flag) != 0;
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
