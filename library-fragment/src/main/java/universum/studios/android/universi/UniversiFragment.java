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

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.XmlRes;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogFactory;
import universum.studios.android.dialog.manage.DialogXmlFactory;
import universum.studios.android.fragment.ActionBarFragment;

/**
 * An {@link ActionBarFragment} implementation that provides <b>Universi context</b> features via
 * {@link UniversiFragmentDelegate} including other features described below.
 *
 * <h3>1) Data binding</h3>
 * Whether it is used data binding provided by <a href="http://developer.android.com/tools/data-binding/guide.html">Google</a>
 * to bind application logic and layouts or some custom data binding logic, this fragment class provides
 * a simple way to manage data binding requests and to perform actual binding. Whether a new data need
 * to be bound to a view hierarchy of a specific instance of UniversiFragment, {@link #requestBindData()}
 * need to be called. <b>This method can be invoked from any thread.</b> If data binding request has
 * been registered, UniversiFragment will invoke {@link #onBindData()} method whenever its view
 * hierarchy is already created or waits until it is created.
 *
 * <h3>2) Permissions</h3>
 * This fragment class has support for a new permissions management model introduced in the
 * {@link Build.VERSION_CODES#M Marshmallow} Android version. Permissions related methods like
 * {@link #checkSelfPermission(String)} or {@link #supportRequestPermissions(String[], int)} can be
 * invoked regardless of current Android version.
 *
 * @author Martin Albedinsky
 * @since 1.0
 */
public abstract class UniversiFragment extends ActionBarFragment {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiFragment";

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
	 * Runnable that calls {@link #requestBindDataInner()} method.
	 */
	@VisibleForTesting final Runnable REQUEST_BIND_DATA_INNER = new Runnable() {

		/**
		 */
		@Override public void run() {
			requestBindDataInner();
		}
	};

	/**
	 * Delegate that is used to handle requests specific for the Universi context made upon this
	 * fragment like showing and dismissing of dialogs.
	 */
	private UniversiContextDelegate delegate;

	/*
	 * Constructors ================================================================================
	 */

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * Sets a context delegate to be used by this fragment.
	 *
	 * @param delegate The delegate only for testing purpose.
	 */
	@VisibleForTesting void setContextDelegate(final UniversiContextDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Ensures that the context delegate is initialized for this fragment.
	 */
	private void ensureContextDelegate() {
		if (delegate == null) this.delegate = UniversiFragmentDelegate.create(this);
	}

	/**
	 * Sets a controller that should be used to show and dismiss dialogs within context of this fragment.
	 *
	 * @param controller The desired controller. May be {@code null} to use the default one.
	 *
	 * @see #getDialogController()
	 */
	protected void setDialogController(@Nullable DialogController controller) {
		this.ensureContextDelegate();
		this.delegate.setDialogController(controller);
	}

	/**
	 * Returns the controller that can be used to show and dismiss dialogs within context of this
	 * fragment.
	 * <p>
	 * If not specified, instance of {@link DialogController} is instantiated by default.
	 *
	 * @return The dialog controller of this fragment.
	 *
	 * @see #setDialogController(DialogController)
	 */
	@NonNull protected DialogController getDialogController() {
		this.ensureContextDelegate();
		return delegate.getDialogController();
	}

	/**
	 * Specifies a factory that should provide dialog instances that can be parsed from an Xml file
	 * with the specified <var>xmlDialogsSet</var> for {@link DialogController} of this fragment.
	 *
	 * @param xmlDialogsSet Resource id of the desired Xml file containing Xml dialogs that the
	 *                      factory should provide for this fragment. May be {@code 0} to remove the
	 *                      current one.
	 */
	protected void setDialogXmlFactory(@XmlRes final int xmlDialogsSet) {
		this.ensureContextDelegate();
		this.delegate.setDialogXmlFactory(xmlDialogsSet);
	}

	/**
	 * Specifies a factory that should provide dialog instances for {@link DialogController} of
	 * this fragment to show.
	 *
	 * @param factory The desired factory. May be {@code null} to remove the current one.
	 *
	 * @see #getDialogFactory()
	 * @see #showDialogWithId(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	protected void setDialogFactory(@Nullable final DialogFactory factory) {
		this.ensureContextDelegate();
		this.delegate.setDialogFactory(factory);
	}

	/**
	 * Returns the current dialog factory specified for this fragment.
	 *
	 * @return Dialog factory or {@code null} if no factory has been specified yet.
	 *
	 * @see #setDialogFactory(DialogFactory)
	 */
	@Nullable protected DialogFactory getDialogFactory() {
		this.ensureContextDelegate();
		return delegate.getDialogFactory();
	}

	/**
	 */
	@Override public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.ensureContextDelegate();
		this.delegate.setViewCreated(true);
		onBindViews(view, savedInstanceState);
		// Check if there was requested data binding before view creation, if it was, perform binding now.
		if (delegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)) {
			this.delegate.unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
			onBindData();
		}
	}

	/**
	 * Invoked from {@link #onViewCreated(View, Bundle)} to bind all views presented within context
	 * of this fragment.
	 *
	 * @param rootView The root view of this fragment.
	 */
	protected void onBindViews(@NonNull final View rootView, @Nullable final Bundle savedInstanceState) {
		// Inheritance hierarchies may perform here views binding/injection.
	}

	/**
	 */
	@Override public void onResume() {
		super.onResume();
		this.ensureContextDelegate();
		this.delegate.setStateSaved(false);
		this.delegate.setPaused(false);
	}

	/**
	 * Requests performing of data binding specific for this fragment via {@link #onBindData()}.
	 * If this fragment has its view hierarchy already created {@link #onBindData()} will be invoked
	 * immediately, otherwise will wait until {@link #onViewCreated(View, Bundle)} is invoked.
	 * <p>
	 * <b>This method may be invoked also from a background-thread</b>.
	 */
	protected void requestBindData() {
		// Check whether this call has been made on the UI thread, if not post on the UI thread the request runnable.
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			this.requestBindDataInner();
		} else {
			runOnUiThread(REQUEST_BIND_DATA_INNER);
		}
	}

	/**
	 * Performs data binding of this fragment. Will invoke {@link #onBindData()} if view hierarchy of
	 * this fragment is already created, otherwise will register a binding request via {@link UniversiContextDelegate#registerRequest(int)}.
	 */
	final void requestBindDataInner() {
		this.ensureContextDelegate();
		if (delegate.isViewCreated()) {
			onBindData();
			return;
		}
		this.delegate.registerRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
	}

	/**
	 * Invoked due to call to {@link #requestBindData()} to perform data binding specific for this
	 * fragment instance.
	 * <p>
	 * <b>This is always invoked on the UI thread.</b>
	 */
	@UiThread protected void onBindData() {
		// Inheritance hierarchies may perform theirs specific data binding logic here.
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
	@CheckResult protected boolean isActiveNetworkConnected() {
		this.ensureContextDelegate();
		return delegate.isActiveNetworkConnected();
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
	@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
	@CheckResult protected boolean isNetworkConnected(final int networkType) {
		this.ensureContextDelegate();
		return delegate.isNetworkConnected(networkType);
	}

	/**
	 * Delegated call to {@link ActivityCompat#checkSelfPermission(Context, String)}.
	 *
	 * @param permission The desired permission for which to perform check.
	 * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
	 * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
	 */
	@CheckResult protected int checkSelfPermission(@NonNull final String permission) {
		return ActivityCompat.checkSelfPermission(getActivity(), permission);
	}

	/**
	 */
	@Override @CheckResult public boolean shouldShowRequestPermissionRationale(@NonNull final String permission) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && super.shouldShowRequestPermissionRationale(permission);
	}

	/**
	 * Invokes {@link #requestPermissions(String[], int)} on Android versions above {@link Build.VERSION_CODES#M Marshmallow}
	 * (including).
	 * <p>
	 * Calling this method on Android versions before <b>MARSHMALLOW</b> will be ignored.
	 *
	 * @param permissions The desired set of permissions to request.
	 * @param requestCode Code to identify this request in {@link #onRequestPermissionsResult(int, String[], int[])}.
	 */
	protected void supportRequestPermissions(@NonNull final String[] permissions, final int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			requestPermissions(permissions, requestCode);
	}

	/**
	 */
	@Override public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 * Same as {@link #showDialogWithId(int, DialogOptions)} with {@code null} options.
	 */
	protected boolean showDialogWithId(@IntRange(from = 0) final int dialogId) {
		return showDialogWithId(dialogId, null);
	}

	/**
	 * Shows a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to show.
	 * @param options  Options for the dialog.
	 * @return {@code True} if dialog has been shown, {@code false} if this fragment is currently
	 * <b>paused</b> or does not have its dialog factory specified.
	 *
	 * @see DialogController#newRequest(int)
	 * @see #setDialogFactory(DialogFactory)
	 * @see #dismissDialogWithId(int)
	 */
	protected boolean showDialogWithId(@IntRange(from = 0) final int dialogId, @Nullable final DialogOptions options) {
		this.ensureContextDelegate();
		return delegate.showDialogWithId(dialogId, options);
	}

	/**
	 * Dismisses a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if this fragment is currently
	 * <b>paused</b> or does not have its dialog factory specified.
	 *
	 * @see DialogController#newRequest(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	protected boolean dismissDialogWithId(@IntRange(from = 0) final int dialogId) {
		this.ensureContextDelegate();
		return delegate.dismissDialogWithId(dialogId);
	}

	/**
	 * Same as {@link #showXmlDialog(int, DialogOptions)} with {@code null} options.
	 */
	protected boolean showXmlDialog(@XmlRes final int resId) {
		return showXmlDialog(resId, null);
	}

	/**
	 * Like {@link #showDialogWithId(int, DialogOptions)}, but in this case will be used internal
	 * instance of {@link DialogXmlFactory} to create (inflate) the desired dialog instance to be
	 * shown.
	 *
	 * @param resId   Resource id of Xml file containing the desired dialog (its specification) to show.
	 * @param options Options for the dialog.
	 * @return {@code True} if dialog has been successfully inflated and shown, {@code false} if
	 * this fragment is currently <b>paused</b> or dialog failed to be inflated.
	 *
	 * @see DialogXmlFactory#createDialog(int, DialogOptions)
	 * @see #dismissXmlDialog(int)
	 */
	protected boolean showXmlDialog(@XmlRes final int resId, @Nullable final DialogOptions options) {
		this.ensureContextDelegate();
		return delegate.showXmlDialog(resId, options);
	}

	/**
	 * Dismisses an Xml dialog that has been shown via {@link #showXmlDialog(int, DialogOptions)}.
	 *
	 * @param resId Resource id of Xml file containing the desired dialog (its specification) to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if this fragment is currently
	 * <b>paused</b>.
	 *
	 * @see #showXmlDialog(int, DialogOptions)
	 */
	protected boolean dismissXmlDialog(@XmlRes final int resId) {
		this.ensureContextDelegate();
		return delegate.dismissXmlDialog(resId);
	}

	/**
	 */
	@Override public void onSaveInstanceState(@NonNull final Bundle outState) {
		super.onSaveInstanceState(outState);
		this.ensureContextDelegate();
		this.delegate.setStateSaved(true);
	}

	/**
	 */
	@Override public void onPause() {
		super.onPause();
		this.ensureContextDelegate();
		this.delegate.setPaused(true);
	}

	/**
	 */
	@Override public void onDestroyView() {
		super.onDestroyView();
		if (delegate != null) delegate.setViewCreated(false);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}