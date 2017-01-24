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
package universum.studios.android.universi;

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
import android.support.annotation.UiThread;
import android.support.annotation.XmlRes;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.manage.DialogController;
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
 * <h3>1) Permissions</h3>
 * This fragment class has support for a new permissions management model introduced in the
 * {@link Build.VERSION_CODES#M Marshmallow} Android version. Permissions related methods like
 * {@link #checkSelfPermission(String)} or {@link #supportRequestPermissions(String[], int)} can be
 * invoked regardless of current Android version.
 *
 * @author Martin Albedinsky
 */
public abstract class UniversiFragment extends ActionBarFragment {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiFragment";

	/**
	 * Flag indicating whether the output trough log-cat is enabled or not.
	 */
	// private final boolean LOG_ENABLED = true;

	/**
	 * Flag indicating whether the debug output trough log-cat is enabled or not.
	 */
	// private final boolean DEBUG_ENABLED = true;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Delegate that is used to handle requests specific for the Universi context made upon this
	 * fragment like showing and dismissing of dialogs.
	 */
	private UniversiFragmentDelegate mContextDelegate;

	/**
	 * Runnable invoking {@link #requestBindDataInner()} method.
	 */
	private Runnable mRequestBindDataInner;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Methods =====================================================================================
	 */

	/**
	 * Sets a controller that should be used to show and dismiss dialogs within context of this fragment.
	 *
	 * @param controller The desired controller. Can be {@code null} to use the default one.
	 * @see #getDialogController()
	 */
	protected void setDialogController(@Nullable DialogController controller) {
		this.ensureContextDelegate();
		mContextDelegate.setDialogController(controller);
	}

	/**
	 * Returns the controller that can be used to show and dismiss dialogs within context of this
	 * fragment.
	 * <p>
	 * If not specified, instance of {@link DialogController} is instantiated by default.
	 *
	 * @return The dialog controller of this fragment.
	 * @see #setDialogController(DialogController)
	 */
	@NonNull
	protected DialogController getDialogController() {
		this.ensureContextDelegate();
		return mContextDelegate.getDialogController();
	}

	/**
	 * Specifies a factory that should provide dialog instances that can be parsed from an Xml file
	 * with the specified <var>xmlDialogsSet</var> for {@link DialogController} of this fragment.
	 *
	 * @param xmlDialogsSet Resource id of the desired Xml file containing Xml dialogs that the
	 *                      factory should provide for this fragment. May be {@code 0} to remove the
	 *                      current one.
	 */
	protected void setDialogXmlFactory(@XmlRes int xmlDialogsSet) {
		this.ensureContextDelegate();
		mContextDelegate.setDialogXmlFactory(xmlDialogsSet);
	}

	/**
	 * Specifies a factory that should provide dialog instances for {@link DialogController} of
	 * this fragment to show.
	 *
	 * @param factory The desired factory. Can be {@code null} to remove the current one.
	 * @see #getDialogFactory()
	 * @see #showDialogWithId(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	protected void setDialogFactory(@Nullable DialogController.DialogFactory factory) {
		this.ensureContextDelegate();
		mContextDelegate.setDialogFactory(factory);
	}

	/**
	 * Returns the current dialog factory specified for this fragment.
	 *
	 * @return Dialog factory or {@code null} if no factory has been specified yet.
	 * @see #setDialogFactory(DialogController.DialogFactory)
	 */
	@Nullable
	protected DialogController.DialogFactory getDialogFactory() {
		this.ensureContextDelegate();
		return mContextDelegate.getDialogFactory();
	}

	/**
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		this.ensureContextDelegate();
		mContextDelegate.setViewCreated(true);
		onBindViews(view, savedInstanceState);
		// Check if there was requested data binding before view creation, if it was, perform binding now.
		if (mContextDelegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)) {
			mContextDelegate.unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
			onBindData();
		}
	}

	/**
	 * Invoked from {@link #onViewCreated(View, Bundle)} to bind all views presented within context
	 * of this fragment.
	 *
	 * @param rootView The root view of this fragment.
	 */
	protected void onBindViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
	}

	/**
	 */
	@Override
	public void onResume() {
		super.onResume();
		this.ensureContextDelegate();
		mContextDelegate.setPaused(false);
	}

	/**
	 * Requests performing of data binding specific for this fragment via {@link #onBindData()}.
	 * If this fragment has its view hierarchy already created {@link #onBindData()} will be invoked
	 * immediately, otherwise will wait until {@link #onViewCreated(View, Bundle)} is invoked.
	 * <p>
	 * <b>This method can be invoked from a background-thread</b>.
	 */
	protected void requestBindData() {
		// Check whether this call has been made on the UI thread, if not post on the UI thread the request runnable.
		if (Looper.getMainLooper().equals(Looper.myLooper())) {
			this.requestBindDataInner();
		} else {
			if (mRequestBindDataInner == null) {
				this.mRequestBindDataInner = new Runnable() {
					@Override
					public void run() {
						requestBindDataInner();
					}
				};
			}
			runOnUiThread(mRequestBindDataInner);
		}
	}

	/**
	 * Performs data binding of this fragment. Will invoke {@link #onBindData()} if view hierarchy of
	 * this fragment is already created, otherwise will register a binding request via {@link UniversiContextDelegate#registerRequest(int)}.
	 */
	final void requestBindDataInner() {
		this.ensureContextDelegate();
		if (mContextDelegate.isViewCreated()) {
			onBindData();
			return;
		}
		mContextDelegate.registerRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
	}

	/**
	 * Invoked due to call to {@link #requestBindData()} to perform data binding specific for this
	 * fragment instance.
	 * <p>
	 * <b>This is always invoked on the UI thread.</b>
	 */
	@UiThread
	protected void onBindData() {
	}

	/**
	 * Checks whether the current active network is at this time connected or not.
	 *
	 * @return {@code True} if active network is connected, {@code false} otherwise.
	 * @see ConnectivityManager#getActiveNetworkInfo()
	 * @see NetworkInfo#isConnected()
	 * @see #isNetworkConnected(int)
	 */
	@CheckResult
	protected boolean isActiveNetworkConnected() {
		this.ensureContextDelegate();
		return mContextDelegate.isActiveNetworkConnected();
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
	@CheckResult
	protected boolean isNetworkConnected(int networkType) {
		this.ensureContextDelegate();
		return mContextDelegate.isNetworkConnected(networkType);
	}

	/**
	 * Delegated call to {@link ActivityCompat#checkSelfPermission(Context, String)}.
	 *
	 * @param permission The desired permission for which to perform check.
	 * @return {@link android.content.pm.PackageManager#PERMISSION_GRANTED} if you have the
	 * permission, or {@link android.content.pm.PackageManager#PERMISSION_DENIED} if not.
	 */
	@CheckResult
	protected int checkSelfPermission(@NonNull String permission) {
		return ActivityCompat.checkSelfPermission(getActivity(), permission);
	}

	/**
	 */
	@Override
	@CheckResult
	public boolean shouldShowRequestPermissionRationale(@NonNull String permission) {
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
	protected void supportRequestPermissions(@NonNull String[] permissions, int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(permissions, requestCode);
	}

	/**
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 * Same as {@link #showDialogWithId(int, DialogOptions)} with {@code null} options.
	 */
	protected boolean showDialogWithId(@IntRange(from = 0) int dialogId) {
		return showDialogWithId(dialogId, null);
	}

	/**
	 * Shows a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to show.
	 * @param options  Options for the dialog.
	 * @return {@code True} if dialog has been shown, {@code false} if this fragment is currently
	 * <b>paused</b> or does not have its dialog factory specified.
	 * @see DialogController#showDialog(int, DialogOptions)
	 * @see #setDialogFactory(DialogController.DialogFactory)
	 * @see #dismissDialogWithId(int)
	 */
	protected boolean showDialogWithId(@IntRange(from = 0) int dialogId, @Nullable DialogOptions options) {
		this.ensureContextDelegate();
		return mContextDelegate.showDialogWithId(dialogId, options);
	}

	/**
	 * Dismisses a dialog that is provided by the current dialog factory under the specified <var>dialogId</var>.
	 *
	 * @param dialogId Id of the desired dialog to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if this fragment is currently
	 * <b>paused</b> or does not have its dialog factory specified.
	 * @see DialogController#dismissDialog(int)
	 * @see #showDialogWithId(int, DialogOptions)
	 */
	protected boolean dismissDialogWithId(@IntRange(from = 0) int dialogId) {
		this.ensureContextDelegate();
		return mContextDelegate.dismissDialogWithId(dialogId);
	}

	/**
	 * Same as {@link #showXmlDialog(int, DialogOptions)} with {@code null} options.
	 */
	protected boolean showXmlDialog(@XmlRes int resId) {
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
	 * @see DialogXmlFactory#createDialogInstance(int, DialogOptions)
	 * @see #dismissXmlDialog(int)
	 */
	protected boolean showXmlDialog(@XmlRes int resId, @Nullable DialogOptions options) {
		this.ensureContextDelegate();
		return mContextDelegate.showXmlDialog(resId, options);
	}

	/**
	 * Dismisses an Xml dialog that has been shown via {@link #showXmlDialog(int, DialogOptions)}.
	 *
	 * @param resId Resource id of Xml file containing the desired dialog (its specification) to dismiss.
	 * @return {@code True} if dialog has been dismissed, {@code false} if this fragment is currently
	 * <b>paused</b>.
	 * @see #showXmlDialog(int, DialogOptions)
	 */
	protected boolean dismissXmlDialog(@XmlRes int resId) {
		this.ensureContextDelegate();
		return mContextDelegate.dismissXmlDialog(resId);
	}

	/**
	 */
	@Override
	public void onPause() {
		super.onPause();
		this.ensureContextDelegate();
		mContextDelegate.setPaused(true);
	}

	/**
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		this.ensureContextDelegate();
		mContextDelegate.setViewCreated(false);
	}

	/**
	 * Ensures that the context delegate is initialized for this fragment.
	 */
	private void ensureContextDelegate() {
		if (mContextDelegate == null) this.mContextDelegate = UniversiContextDelegate.create(this);
	}

	/**
	 * Inner classes ===============================================================================
	 */
}
