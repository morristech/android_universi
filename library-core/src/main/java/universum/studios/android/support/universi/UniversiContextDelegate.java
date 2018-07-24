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
package universum.studios.android.support.universi;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.XmlRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import universum.studios.android.support.dialog.DialogOptions;
import universum.studios.android.support.dialog.XmlDialog;
import universum.studios.android.support.dialog.manage.DialogController;
import universum.studios.android.support.dialog.manage.DialogFactory;
import universum.studios.android.support.dialog.manage.DialogRequest;
import universum.studios.android.support.dialog.manage.DialogXmlFactory;

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
 * @since 1.0
 */
public abstract class UniversiContextDelegate {

	/*
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
	 * Flag indicating whether state for the wrapped context has been already saved or not.
	 */
	private static final int PFLAG_STATE_SAVED = 0x00000001 << 1;

	/**
	 * Flag indicating whether the wrapped context is paused or not.
	 */
	private static final int PFLAG_PAUSED = 0x00000001 << 2;

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
	 * Context that is used to access some application data and services.
	 */
	final Context context;

	/**
	 * Set of private flags specified for this delegate.
	 */
	private int privateFlags;

	/**
	 * Set of request flags.
	 */
	private int requestFlags;

	/**
	 * Controller that is used to show and dismiss dialogs within context that uses this delegate.
	 */
	private DialogController dialogController;

	/**
	 * Factory providing dialog instances for the {@link #dialogController}.
	 */
	private DialogFactory dialogFactory;

	/**
	 * Dialog factory used to inflate dialog instances presented within a single Xml file.
	 */
	private DialogXmlFactory dialogXmlFactory;

	/**
	 * Manager used to check for established/available connections.
	 */
	private ConnectivityManager connectivityManager;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiContextDelegate.
	 *
	 * @param context Context that is used to access some application data and services.
	 */
	UniversiContextDelegate(@NonNull final Context context) {
		this.context = context;
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * Sets a controller that should be used to show and dismiss dialogs within context that uses
	 * this delegate.
	 *
	 * @param controller The desired controller. May be {@code null} to use the default one.
	 *
	 * @see #getDialogController()
	 */
	public void setDialogController(@Nullable final DialogController controller) {
		this.dialogController = controller;
		if (dialogFactory != null) {
			this.ensureDialogController();
			dialogController.setFactory(dialogFactory);
		}
	}

	/**
	 * Returns the controller that can be used to show and dismiss dialogs within context that uses
	 * this delegate
	 * <p>
	 * If not specified, instance of {@link DialogController} is instantiated by default.
	 *
	 * @return The dialog controller of this delegate.
	 *
	 * @see #setDialogController(DialogController)
	 */
	@NonNull public DialogController getDialogController() {
		this.ensureDialogController();
		return dialogController;
	}

	/**
	 * Ensures that the dialog controller is initialized.
	 */
	private void ensureDialogController() {
		if (dialogController == null) this.dialogController = instantiateDialogController();
	}

	/**
	 * Creates a new instance of DialogController for the context that uses this delegate.
	 *
	 * @return New DialogController instance.
	 */
	@NonNull abstract DialogController instantiateDialogController();

	/**
	 * Same as {@link #setDialogFactory(DialogFactory)} where will be passed instance of
	 * {@link DialogXmlFactory} with the specified <var>xmlDialogsSet</var>.
	 *
	 * @param xmlDialogsSet Resource id of the desired Xml file containing Xml dialogs that the
	 *                      factory should provide. May be {@code 0} to remove the current one.
	 */
	public void setDialogXmlFactory(@XmlRes final int xmlDialogsSet) {
		setDialogFactory(xmlDialogsSet == 0 ? null : new DialogXmlFactory(context, xmlDialogsSet));
	}

	/**
	 * Specifies a factory that should provide dialog instances for {@link DialogController} of
	 * this delegate.
	 *
	 * @param factory The desired factory. May be {@code null} to remove the current one.
	 *
	 * @see #getDialogFactory()
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	public void setDialogFactory(@Nullable final DialogFactory factory) {
		this.dialogFactory = factory;
		this.ensureDialogController();
		dialogController.setFactory(factory);
	}

	/**
	 * Returns the current dialog factory specified for this delegate.
	 *
	 * @return Dialog factory or {@code null} if no factory has been specified yet.
	 *
	 * @see #setDialogFactory(DialogFactory)
	 */
	@Nullable public DialogFactory getDialogFactory() {
		return dialogFactory;
	}

	/**
	 * Shows a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to show.
	 * @param options  Options for the dialog.
	 * @return {@code True} if dialog has been shown, {@code false} if context that uses this delegate
	 * is currently <b>paused</b> or its <b>state has been already saved</b> or does not have its dialog
	 * factory specified.
	 *
	 * @see DialogController#newRequest(int)
	 * @see #setDialogFactory(DialogFactory)
	 * @see #dismissDialogWithId(int)
	 */
	public boolean showDialogWithId(@IntRange(from = 0) final int dialogId, @Nullable final DialogOptions options) {
		if (hasPrivateFlag(PFLAG_STATE_SAVED | PFLAG_PAUSED) || dialogFactory == null) {
			return false;
		}
		this.ensureDialogController();
		return dialogController.newRequest(dialogId).options(options).execute() != null;
	}

	/**
	 * Dismisses a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if context that uses this
	 * delegate is currently <b>paused</b> or does not have its dialog factory specified.
	 *
	 * @see DialogController#newRequest(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	public boolean dismissDialogWithId(@IntRange(from = 0) final int dialogId) {
		if (hasPrivateFlag(PFLAG_PAUSED) || dialogFactory == null) {
			return false;
		}
		this.ensureDialogController();
		return dialogController.newRequest(dialogId).intent(DialogRequest.DISMISS).allowStateLoss(true).execute() != null;
	}

	/**
	 * Like {@link #showDialogWithId(int, DialogOptions)}, but in this case will be used internal
	 * instance of {@link DialogXmlFactory} to create (inflate) the desired dialog instance to be
	 * shown.
	 *
	 * @param resId   Resource id of Xml file containing the desired dialog (its specification) to show.
	 * @param options Options for the dialog.
	 * @return {@code True} if dialog has been successfully inflated and shown, {@code false} if
	 * context that uses this delegate is currently <b>paused</b> or its <b>state has been already saved</b>
	 * or dialog failed to be inflated.
	 *
	 * @see DialogXmlFactory#createDialog(int, DialogOptions)
	 * @see #dismissXmlDialog(int)
	 */
	public boolean showXmlDialog(@XmlRes final int resId, @Nullable final DialogOptions options) {
		if (hasPrivateFlag(PFLAG_STATE_SAVED | PFLAG_PAUSED)) {
			return false;
		}
		final DialogXmlFactory dialogFactory = accessDialogXmlFactory();
		final DialogFragment dialog = dialogFactory.createDialog(resId, options);
		if (dialog == null) {
			return false;
		}
		this.ensureDialogController();
		return dialogController.newRequest(dialog).tag(dialogFactory.createDialogTag(resId)).execute() != null;
	}

	/**
	 * Dismisses an Xml dialog that has been shown via {@link #showXmlDialog(int, DialogOptions)}.
	 *
	 * @param resId Resource id of Xml file containing the desired dialog (its specification) to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if context that uses this
	 * delegate is currently <b>paused</b>.
	 *
	 * @see #showXmlDialog(int, DialogOptions)
	 */
	public boolean dismissXmlDialog(@XmlRes final int resId) {
		if (hasPrivateFlag(PFLAG_PAUSED)) {
			return false;
		}
		this.ensureDialogController();
		final Fragment fragment = dialogController.getFragmentManager().findFragmentByTag(accessDialogXmlFactory().createDialogTag(resId));
		return fragment instanceof DialogFragment && dialogController.newRequest((DialogFragment) fragment)
				.intent(DialogRequest.DISMISS)
				.allowStateLoss(true)
				.execute() != null;
	}

	/**
	 * Ensures that the dialog Xml factory is initialized and returns it. If {@link #dialogFactory}
	 * is instance of {@link DialogXmlFactory} than it will be returned.
	 *
	 * @return Instance of DialogXmlFactory that can be used to create instances of Xml dialogs.
	 */
	private DialogXmlFactory accessDialogXmlFactory() {
		if (dialogFactory instanceof DialogXmlFactory) {
			return (DialogXmlFactory) dialogFactory;
		}
		if (dialogXmlFactory == null) {
			dialogXmlFactory = new DialogXmlFactory(context);
		}
		return dialogXmlFactory;
	}

	/**
	 * Checks whether the current active network is at this time connected or not.
	 *
	 * @return {@code True} if active network is connected, {@code false} otherwise.
	 *
	 * @see ConnectivityManager#getActiveNetworkInfo()
	 * @see NetworkInfo#isConnected()
	 * @see #isNetworkConnected(int)
	 */
	@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
	public boolean isActiveNetworkConnected() {
		this.ensureConnectivityManager();
		final NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}

	/**
	 * Checks whether a network with the specified <var>networkType</var> is at this time connected
	 * or no.
	 *
	 * @param networkType The desired network type to check for connection.
	 * @return {@code True} if the requested network is connected, {@code false} otherwise.
	 *
	 * @see ConnectivityManager#getNetworkInfo(int)
	 * @see NetworkInfo#isConnected()
	 */
	@SuppressWarnings("deprecation")
	@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
	public boolean isNetworkConnected(final int networkType) {
		this.ensureConnectivityManager();
		final NetworkInfo info = connectivityManager.getNetworkInfo(networkType);
		return info != null && info.isConnected();
	}

	/**
	 * Ensures that the connectivity manager is initialized.
	 */
	private void ensureConnectivityManager() {
		if (connectivityManager == null) {
			final Context applicationContext = context.getApplicationContext();
			this.connectivityManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
	}

	/**
	 * Sets a boolean flag indicating whether a view hierarchy of the associated context is created or not.
	 *
	 * @param created {@code True} if view hierarchy is created, {@code false} otherwise.
	 *
	 * @see #isViewCreated()
	 */
	public void setViewCreated(final boolean created) {
		this.updatePrivateFlags(PFLAG_VIEW_CREATED, created);
	}

	/**
	 * Returns the boolean flag indicating whether view hierarchy of the associated context is created
	 * or not.
	 *
	 * @return {@code True} if view is created, {@code false} otherwise.
	 *
	 * @see #setViewCreated(boolean)
	 */
	public boolean isViewCreated() {
		return hasPrivateFlag(PFLAG_VIEW_CREATED);
	}

	/**
	 * Sets a boolean flag indicating whether state of the associated context has been saved or not.
	 *
	 * @param saved {@code True} if state has been saved, {@code false} otherwise.
	 *
	 * @see #isStateSaved()
	 */
	public void setStateSaved(final boolean saved) {
		this.updatePrivateFlags(PFLAG_STATE_SAVED, saved);
	}

	/**
	 * Returns the boolean flag indicating whether state of the associated context has been already
	 * saved or not.
	 * <p>
	 * This should be {@code true} after state of context is saved and before that context is resumed.
	 *
	 * @return {@code True} if state has been saved, {@code false} otherwise.
	 *
	 * @see #setStateSaved(boolean)
	 */
	public boolean isStateSaved() {
		return hasPrivateFlag(PFLAG_STATE_SAVED);
	}

	/**
	 * Sets a boolean flag indicating whether the associated context has been paused or not.
	 *
	 * @param paused {@code True} if context is paused, {@code false} otherwise.
	 *
	 * @see #isPaused() ()
	 */
	public void setPaused(final boolean paused) {
		this.updatePrivateFlags(PFLAG_PAUSED, paused);
	}

	/**
	 * Returns the boolean flag indicating whether the related context is paused or not.
	 * <p>
	 * This should be {@code true} after context is paused and before it is again resumed.
	 *
	 * @return {@code True} if context is paused, {@code false} otherwise.
	 *
	 * @see #setPaused(boolean)
	 */
	public boolean isPaused() {
		return hasPrivateFlag(PFLAG_PAUSED);
	}

	/**
	 * Adds a request with the specified <var>request</var> flag into the registered ones.
	 *
	 * @param requestFlag The request flag that should be registered.
	 *
	 * @see #isRequestRegistered(int)
	 * @see #unregisterRequest(int)
	 */
	public void registerRequest(final int requestFlag) {
		this.requestFlags |= requestFlag;
	}

	/**
	 * Removes request with the specified <var>request</var> flag from the registered ones.
	 *
	 * @param requestFlag The request flag that should be unregistered.
	 *
	 * @see #registerRequest(int)
	 * @see #isRequestRegistered(int)
	 */
	public void unregisterRequest(final int requestFlag) {
		this.requestFlags &= ~requestFlag;
	}

	/**
	 * Checks whether a request with the specified <var>request</var> flag is registered or not.
	 *
	 * @param requestFlag The request flag for which to check if it is registered.
	 * @return {@code True} if request is registered, {@code false} otherwise.
	 *
	 * @see #registerRequest(int)
	 * @see #unregisterRequest(int)
	 */
	public boolean isRequestRegistered(final int requestFlag) {
		return (requestFlags & requestFlag) != 0;
	}

	/**
	 * Updates the current private flags.
	 *
	 * @param flag Value of the desired flag to add/remove to/from the current private flags.
	 * @param add  Boolean flag indicating whether to add or remove the specified <var>flag</var>.
	 */
	private void updatePrivateFlags(final int flag, final boolean add) {
		if (add) this.privateFlags |= flag;
		else this.privateFlags &= ~flag;
	}

	/**
	 * Returns a boolean flag indicating whether the specified <var>flag</var> is contained within
	 * the current private flags or not.
	 *
	 * @param flag Value of the flag to check.
	 * @return {@code True} if the requested flag is contained, {@code false} otherwise.
	 */
	private boolean hasPrivateFlag(final int flag) {
		return (privateFlags & flag) != 0;
	}

	/*
	 * Inner classes ===============================================================================
	 */
}