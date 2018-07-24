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
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;
import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;
import android.support.annotation.XmlRes;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.widget.Toolbar;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogFactory;
import universum.studios.android.fragment.ActionBarDelegate;
import universum.studios.android.fragment.BackPressWatcher;
import universum.studios.android.fragment.annotation.ActionBarOptions;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.annotation.MenuOptions;
import universum.studios.android.fragment.annotation.handler.ActionBarAnnotationHandlers;
import universum.studios.android.fragment.annotation.handler.ActionBarFragmentAnnotationHandler;
import universum.studios.android.fragment.manage.FragmentController;
import universum.studios.android.fragment.manage.FragmentFactory;
import universum.studios.android.transition.BaseNavigationalTransition;

/**
 * An {@link Activity} implementation that provides <b>Universi context</b> features via
 * {@link UniversiActivityDelegate} including other features described below.
 *
 * <h3>1) Data binding</h3>
 * Whether it is used data binding provided by <a href="http://developer.android.com/tools/data-binding/guide.html">Google</a>
 * to bind application logic and layouts or some custom data binding logic, this activity class provides
 * a simple way to manage data binding requests and to perform actual binding. Whether a new data need
 * to be bound to a view hierarchy of a specific instance of UniversiActivity, {@link #requestBindData()}
 * need to be called. <b>This method can be invoked from any thread.</b> If data binding request has
 * been registered, UniversiActivity will invoke {@link #onBindData()} method whenever its view
 * hierarchy is already created or waits until it is created.
 *
 * <h3>1) Permissions</h3>
 * This activity class has support for a new permissions management model introduced in the
 * {@link Build.VERSION_CODES#M Marshmallow} Android version. Permissions related methods like
 * {@link #checkSelfPermission(String)} or {@link #supportRequestPermissions(String[], int)} can be
 * invoked regardless of current Android version.
 *
 * @author Martin Albedinsky
 * @since 1.0
 *
 * @see UniversiCompatActivity
 * @see UniversiFragment
 */
public abstract class UniversiActivity extends Activity implements UniversiActivityContext {

	/*
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiActivity";

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
	 * Handler responsible for processing of all annotations of this class and also for handling all
	 * annotations related operations for this class.
	 */
	final ActionBarFragmentAnnotationHandler annotationHandler;

	/**
	 * Delegate that is used to handle requests specific for the Universi context made upon this activity
	 * like showing and dismissing of dialogs or showing and hiding of fragments.
	 */
	private UniversiActivityDelegate delegate;

	/*
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiActivity. If annotations processing is enabled via
	 * {@link FragmentAnnotations#setEnabled(boolean)} all annotations supported by this activity
	 * class will be processed/obtained here so they can be later used.
	 */
	public UniversiActivity() {
		super();
		this.annotationHandler = onCreateAnnotationHandler();
	}

	/*
	 * Methods =====================================================================================
	 */

	/**
	 * Invoked to create annotations handler for this instance.
	 *
	 * @return Annotations handler specific for this class.
	 */
	ActionBarFragmentAnnotationHandler onCreateAnnotationHandler() {
		return ActionBarAnnotationHandlers.obtainActionBarFragmentHandler(getClass());
	}

	/**
	 * Returns handler that is responsible for annotations processing of this class and also for
	 * handling all annotations related operations for this class.
	 *
	 * @return Annotations handler specific for this class.
	 * @throws IllegalStateException If annotations processing is not enabled for the Fragments library.
	 */
	@NonNull protected ActionBarFragmentAnnotationHandler getAnnotationHandler() {
		FragmentAnnotations.checkIfEnabledOrThrow();
		return annotationHandler;
	}

	/**
	 * Sets a context delegate to be used by this activity.
	 *
	 * @param delegate The delegate only for testing purpose.
	 */
	@VisibleForTesting void setContextDelegate(final UniversiActivityDelegate delegate) {
		this.delegate = delegate;
	}

	/**
	 * Ensures that the context delegate is initialized for this activity.
	 */
	private void ensureContextDelegate() {
		if (delegate == null) this.delegate = UniversiActivityDelegate.create(this);
	}

	/**
	 * Returns the context delegate created by this activity or specified via {@link #setContextDelegate(UniversiActivityDelegate)}.
	 *
	 * @return This activity's context delegate.
	 */
	@VisibleForTesting UniversiActivityDelegate getContextDelegate() {
		this.ensureContextDelegate();
		return delegate;
	}

	/**
	 */
	@Override protected void onCreate(@Nullable final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (annotationHandler != null) {
			final int viewResource = annotationHandler.getContentViewResource(-1);
			if (viewResource != -1) {
				setContentView(viewResource);
			}
		}
	}

	/**
	 */
	@Deprecated
	@Override @Nullable public <D> Loader<D> startLoader(
			@IntRange(from = 0) final int id,
			@Nullable final Bundle params,
			@NonNull final LoaderManager.LoaderCallbacks<D> callbacks
	) {
		this.ensureContextDelegate();
		return delegate.startLoader(id, params, callbacks);
	}

	/**
	 */
	@Deprecated
	@Override @Nullable public <D> Loader<D> initLoader(
			@IntRange(from = 0) final int id,
			@Nullable final Bundle params,
			@NonNull final LoaderManager.LoaderCallbacks<D> callbacks
	) {
		this.ensureContextDelegate();
		return delegate.initLoader(id, params, callbacks);
	}

	/**
	 */
	@Deprecated
	@Override @Nullable public <D> Loader<D> restartLoader(
			@IntRange(from = 0) final int id,
			@Nullable final Bundle params,
			@NonNull final LoaderManager.LoaderCallbacks<D> callbacks
	) {
		this.ensureContextDelegate();
		return delegate.restartLoader(id, params, callbacks);
	}

	/**
	 */
	@Deprecated
	@Override public void destroyLoader(@IntRange(from = 0) final int id) {
		this.ensureContextDelegate();
		this.delegate.destroyLoader(id);
	}

	/**
	 */
	@Override public void onContentChanged() {
		super.onContentChanged();
		this.ensureContextDelegate();
		this.delegate.setViewCreated(true);
		this.configureActionBar(getActionBar());
		onBindViews();
		// Check if there was requested data binding before view creation, if it was, perform binding now.
		if (delegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)) {
			this.delegate.unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
			onBindData();
		}
	}

	/**
	 */
	@Override public void setActionBar(@Nullable final Toolbar toolbar) {
		super.setActionBar(toolbar);
		this.configureActionBar(getActionBar());
	}

	/**
	 * Called to configure the given <var>actionBar</var> according to the {@link ActionBarOptions @ActionBarOptions}
	 * annotation (if presented).
	 */
	private void configureActionBar(final ActionBar actionBar) {
		if (actionBar == null || annotationHandler == null) {
			return;
		}
		this.annotationHandler.configureActionBar(ActionBarDelegate.create(this, actionBar));
	}

	/**
	 * Invoked from {@link #onContentChanged()} to bind all views presented within context of this
	 * activity.
	 */
	protected void onBindViews() {
		// Inheritance hierarchies may perform here views binding/injection.
	}

	/**
	 */
	@Override public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
		if (annotationHandler == null || !annotationHandler.hasOptionsMenu()) {
			return false;
		}
		final int menuResource = annotationHandler.getOptionsMenuResource(-1);
		if (menuResource == -1) {
			return super.onCreateOptionsMenu(menu);
		}
		if (annotationHandler.shouldClearOptionsMenu()) {
			menu.clear();
		}
		switch (annotationHandler.getOptionsMenuFlags(0)) {
			case MenuOptions.IGNORE_SUPER:
				getMenuInflater().inflate(menuResource, menu);
				break;
			case MenuOptions.BEFORE_SUPER:
				getMenuInflater().inflate(menuResource, menu);
				super.onCreateOptionsMenu(menu);
				break;
			case MenuOptions.DEFAULT:
			default:
				super.onCreateOptionsMenu(menu);
				getMenuInflater().inflate(menuResource, menu);
				break;
		}
		return true;
	}

	/**
	 */
	@Override public void setNavigationalTransition(@Nullable final BaseNavigationalTransition transition) {
		this.ensureContextDelegate();
		this.delegate.setNavigationalTransition(transition);
	}

	/**
	 */
	@Override @Nullable public BaseNavigationalTransition getNavigationalTransition() {
		this.ensureContextDelegate();
		return delegate.getNavigationalTransition();
	}

	/**
	 */
	@Override public void setFragmentController(@Nullable final FragmentController controller) {
		this.ensureContextDelegate();
		this.delegate.setFragmentController(controller);
	}

	/**
	 */
	@Override @NonNull public FragmentController getFragmentController() {
		this.ensureContextDelegate();
		return delegate.getFragmentController();
	}

	/**
	 */
	@Override public void setFragmentFactory(@Nullable final FragmentFactory factory) {
		this.ensureContextDelegate();
		this.delegate.setFragmentFactory(factory);
	}

	/**
	 */
	@Override @Nullable public FragmentFactory getFragmentFactory() {
		this.ensureContextDelegate();
		return delegate.getFragmentFactory();
	}

	/**
	 */
	@Override public void setDialogController(@Nullable final DialogController controller) {
		this.ensureContextDelegate();
		this.delegate.setDialogController(controller);
	}

	/**
	 */
	@Override @NonNull public DialogController getDialogController() {
		this.ensureContextDelegate();
		return delegate.getDialogController();
	}

	/**
	 */
	@Override public void setDialogXmlFactory(@XmlRes final int xmlDialogsSet) {
		this.ensureContextDelegate();
		this.delegate.setDialogXmlFactory(xmlDialogsSet);
	}

	/**
	 */
	@Override public void setDialogFactory(@Nullable final DialogFactory factory) {
		this.ensureContextDelegate();
		this.delegate.setDialogFactory(factory);
	}

	/**
	 */
	@Override @Nullable public DialogFactory getDialogFactory() {
		this.ensureContextDelegate();
		return delegate.getDialogFactory();
	}

	/**
	 * Requests performing of data binding specific for this activity via {@link #onBindData()}.
	 * If this activity has its view hierarchy already created {@link #onBindData()} will be invoked
	 * immediately, otherwise will wait until {@link #onContentChanged()} is invoked.
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
	 * Performs data binding of this activity. Will invoke {@link #onBindData()} if view hierarchy of
	 * this activity is already created, otherwise will register a binding request via {@link UniversiContextDelegate#registerRequest(int)}.
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
	 * activity instance.
	 * <p>
	 * <b>This is always invoked on the UI thread.</b>
	 */
	@UiThread protected void onBindData() {
		// Inheritance hierarchies may perform theirs specific data binding logic here.
	}

	/**
	 */
	@Override protected void onResume() {
		super.onResume();
		this.ensureContextDelegate();
		this.delegate.setStateSaved(false);
		this.delegate.setPaused(false);
	}

	/**
	 */
	@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
	@Override @CheckResult public boolean isActiveNetworkConnected() {
		this.ensureContextDelegate();
		return delegate.isActiveNetworkConnected();
	}

	/**
	 */
	@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
	@Override @CheckResult public boolean isNetworkConnected(final int networkType) {
		this.ensureContextDelegate();
		return delegate.isNetworkConnected(networkType);
	}

	/**
	 */
	@Override public int checkSelfPermission(@NonNull final String permission) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
				super.checkSelfPermission(permission) :
				PackageManager.PERMISSION_GRANTED;
	}

	/**
	 */
	@Override public boolean shouldShowRequestPermissionRationale(@NonNull final String permission) {
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
	public void supportRequestPermissions(@NonNull final String[] permissions, final int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			requestPermissions(permissions, requestCode);
	}

	/**
	 */
	@Override public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 */
	@Override public boolean showDialogWithId(final int dialogId) {
		return showDialogWithId(dialogId, null);
	}

	/**
	 */
	@Override public boolean showDialogWithId(final int dialogId, @Nullable final DialogOptions options) {
		this.ensureContextDelegate();
		return delegate.showDialogWithId(dialogId, options);
	}

	/**
	 */
	@Override public boolean dismissDialogWithId(final int dialogId) {
		this.ensureContextDelegate();
		return delegate.dismissDialogWithId(dialogId);
	}

	/**
	 */
	@Override public boolean showXmlDialog(@XmlRes final int resId) {
		return showXmlDialog(resId, null);
	}

	/**
	 */
	@Override public boolean showXmlDialog(@XmlRes final int resId, @Nullable final DialogOptions options) {
		this.ensureContextDelegate();
		return delegate.showXmlDialog(resId, options);
	}

	/**
	 */
	@Override public boolean dismissXmlDialog(@XmlRes final int resId) {
		this.ensureContextDelegate();
		return delegate.dismissXmlDialog(resId);
	}

	/**
	 */
	@Override public void onBackPressed() {
		if (!onBackPress()) finishWithNavigationalTransition();
	}

	/**
	 * Invoked from {@link #onBackPressed()} to give a chance to this activity instance to consume
	 * the back press event before it is delivered to its parent.
	 * <p>
	 * This implementation first tries to dispatch this event to one of its visible fragments via
	 * {@link #dispatchBackPressToFragments()} and if none of the currently visible fragments consumes
	 * the event a stack with fragments will be popped via {@link #popFragmentsBackStack()}.
	 *
	 * @return {@code True} if the back press event has been consumed here, {@code false} otherwise.
	 */
	protected boolean onBackPress() {
		this.ensureContextDelegate();
		return !delegate.isPaused() && (dispatchBackPressToFragments() || popFragmentsBackStack());
	}

	/**
	 * Dispatches back press event to all currently visible fragments displayed in context of this
	 * activity.
	 * <p>
	 * This implementation calls {@link #dispatchBackPressToCurrentFragment()} and returns its result.
	 *
	 * @return {@code True} if some of the visible fragments has consumed the back press event,
	 * {@code false} otherwise.
	 * @see BackPressWatcher#dispatchBackPress()
	 */
	protected boolean dispatchBackPressToFragments() {
		return dispatchBackPressToCurrentFragment();
	}

	/**
	 * Dispatches back press event to the current fragment displayed in context of this activity.
	 *
	 * @return {@code True} if the current fragment has consumed the back press event, {@code false}
	 * otherwise.
	 *
	 * @see #findCurrentFragment()
	 * @see BackPressWatcher#dispatchBackPress()
	 */
	protected boolean dispatchBackPressToCurrentFragment() {
		final Fragment fragment = findCurrentFragment();
		return fragment instanceof BackPressWatcher && ((BackPressWatcher) fragment).dispatchBackPress();
	}

	/**
	 * Finds currently visible fragment that is displayed in context of this activity.
	 * <p>
	 * <b>Note, that implementation of this method assumes that this activity uses {@link FragmentController}
	 * to display its related fragments and that there may be only one fragment visible at a time,
	 * that is in a full screen container.</b>
	 *
	 * @return Current fragment of this activity (that is at this time visible), or {@code null} if
	 * there is no fragment presented within fragment container of this activity at all.
	 */
	@Nullable protected Fragment findCurrentFragment() {
		this.ensureContextDelegate();
		return delegate.findCurrentFragment();
	}

	/**
	 * Pops stack with fragments of this activity.
	 *
	 * @return {@code True} if there was some fragment popped from the stack, {@code false} if there
	 * were no fragments to pop from.
	 */
	protected boolean popFragmentsBackStack() {
		this.ensureContextDelegate();
		return delegate.popFragmentsBackStack();
	}

	/**
	 */
	@Override protected void onSaveInstanceState(@NonNull final Bundle outState) {
		super.onSaveInstanceState(outState);
		this.ensureContextDelegate();
		this.delegate.setStateSaved(true);
	}

	/**
	 */
	@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
	@Override public void onSaveInstanceState(@NonNull final Bundle outState, @NonNull final PersistableBundle outPersistentState) {
		super.onSaveInstanceState(outState, outPersistentState);
		this.ensureContextDelegate();
		this.delegate.setStateSaved(true);
	}

	/**
	 */
	@Override protected void onPause() {
		super.onPause();
		this.ensureContextDelegate();
		this.delegate.setPaused(true);
	}

	/**
	 */
	@Override public boolean finishWithNavigationalTransition() {
		this.ensureContextDelegate();
		if (delegate.finishWithNavigationalTransition()) {
			return true;
		}
		ActivityCompat.finishAfterTransition(this);
		return false;
	}

	/**
	 */
	@Override protected void onDestroy() {
		super.onDestroy();
		if (delegate != null) delegate.setViewCreated(false);
	}

	/*
	 * Inner classes ===============================================================================
	 */
}