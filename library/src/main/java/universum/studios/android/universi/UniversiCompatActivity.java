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

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.XmlRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.manage.DialogController;
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
 * An {@link AppCompatActivity} implementation with same <b>Universi context</b> features provided
 * as {@link UniversiActivity}.
 *
 * @author Martin Albedinsky
 * @see UniversiFragment
 */
public abstract class UniversiCompatActivity extends AppCompatActivity implements UniversiActivityContext {

	/**
	 * Interface ===================================================================================
	 */

	/**
	 * Constants ===================================================================================
	 */

	/**
	 * Log TAG.
	 */
	// private static final String TAG = "UniversiCompatActivity";

	/**
	 * Flag indicating whether the output trough log-cat is enabled or not.
	 */
	// private static final boolean LOG_ENABLED = true;

	/**
	 * Flag indicating whether the debug output trough log-cat is enabled or not.
	 */
	// private static final boolean DEBUG_ENABLED = true;

	/**
	 * Static members ==============================================================================
	 */

	/**
	 * Members =====================================================================================
	 */

	/**
	 * Handler responsible for processing of all annotations of this class and also for handling all
	 * annotations related operations for this class.
	 */
	final ActionBarFragmentAnnotationHandler mAnnotationHandler;

	/**
	 * Delegate that is used to handle requests specific for the Universi context made upon this activity
	 * like showing and dismissing of dialogs or showing and hiding of fragments.
	 */
	private UniversiActivityDelegate mContextDelegate;

	/**
	 * Runnable invoking {@link #requestBindDataInner()} method.
	 */
	private Runnable mRequestBindDataInner;

	/**
	 * Constructors ================================================================================
	 */

	/**
	 * Creates a new instance of UniversiActivity. If annotations processing is enabled via {@link UniversiConfig}
	 * all annotations supported by this activity class will be processed/obtained here so they can
	 * be later used.
	 */
	public UniversiCompatActivity() {
		this.mAnnotationHandler = onCreateAnnotationHandler();
	}

	/**
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
	@NonNull
	protected ActionBarFragmentAnnotationHandler getAnnotationHandler() {
		FragmentAnnotations.checkIfEnabledOrThrow();
		return mAnnotationHandler;
	}

	/**
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mAnnotationHandler != null) {
			final int viewResource = mAnnotationHandler.getContentViewResource(-1);
			if (viewResource != -1) {
				setContentView(viewResource);
			}
		}
	}

	/**
	 */
	@Nullable
	@Override
	public <D> Loader<D> startLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		this.ensureContextDelegate();
		return mContextDelegate.startLoader(id, params, callbacks);
	}

	/**
	 */
	@Nullable
	@Override
	public <D> Loader<D> initLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		this.ensureContextDelegate();
		return mContextDelegate.initLoader(id, params, callbacks);
	}

	/**
	 */
	@Nullable
	@Override
	public <D> Loader<D> restartLoader(@IntRange(from = 0) int id, @Nullable Bundle params, @NonNull LoaderManager.LoaderCallbacks<D> callbacks) {
		this.ensureContextDelegate();
		return mContextDelegate.restartLoader(id, params, callbacks);
	}

	/**
	 */
	@Override
	public void destroyLoader(@IntRange(from = 0) int id) {
		this.ensureContextDelegate();
		mContextDelegate.destroyLoader(id);
	}

	/**
	 */
	@Override
	public void onContentChanged() {
		super.onContentChanged();
		this.ensureContextDelegate();
		mContextDelegate.setViewCreated(true);
		this.configureActionBar(getSupportActionBar());
		onBindViews();
		// Check if there was requested data binding before view creation, if it was, perform binding now.
		if (mContextDelegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)) {
			mContextDelegate.unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
			onBindData();
		}
	}

	/**
	 */
	@Override
	public void setSupportActionBar(@Nullable Toolbar toolbar) {
		super.setSupportActionBar(toolbar);
		this.configureActionBar(getSupportActionBar());
	}

	/**
	 * Called to configure the given <var>actionBar</var> according to the {@link ActionBarOptions @ActionBarOptions}
	 * annotation (if presented).
	 */
	private void configureActionBar(ActionBar actionBar) {
		if (actionBar == null || mAnnotationHandler == null) return;
		mAnnotationHandler.configureActionBar(ActionBarDelegate.create(this, actionBar));
	}

	/**
	 * Invoked from {@link #onContentChanged()} to bind all views presented within context of this
	 * activity.
	 */
	protected void onBindViews() {
	}

	/**
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mAnnotationHandler == null || !mAnnotationHandler.hasOptionsMenu()) return false;
		final int menuResource = mAnnotationHandler.getOptionsMenuResource(-1);
		if (menuResource != -1) {
			if (mAnnotationHandler.shouldClearOptionsMenu()) {
				menu.clear();
			}
			switch (mAnnotationHandler.getOptionsMenuFlags(0)) {
				case MenuOptions.IGNORE_SUPER:
					getMenuInflater().inflate(menuResource, menu);
					break;
				case MenuOptions.BEFORE_SUPER:
					getMenuInflater().inflate(menuResource, menu);
					super.onCreateOptionsMenu(menu);
					break;
				case MenuOptions.DEFAULT:
					super.onCreateOptionsMenu(menu);
					getMenuInflater().inflate(menuResource, menu);
					break;
			}
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 */
	@Override
	public void setNavigationalTransition(@Nullable BaseNavigationalTransition transition) {
		this.ensureContextDelegate();
		mContextDelegate.setNavigationalTransition(transition);
	}

	/**
	 */
	@Override
	@Nullable
	public BaseNavigationalTransition getNavigationalTransition() {
		this.ensureContextDelegate();
		return mContextDelegate.getNavigationalTransition();
	}

	/**
	 */
	@Override
	@NonNull
	public FragmentController getFragmentController() {
		this.ensureContextDelegate();
		return mContextDelegate.getFragmentController();
	}

	/**
	 */
	@Override
	public void setFragmentController(@Nullable FragmentController controller) {
		this.ensureContextDelegate();
		mContextDelegate.setFragmentController(controller);
	}

	/**
	 */
	@Override
	public void setFragmentFactory(@Nullable FragmentFactory factory) {
		this.ensureContextDelegate();
		mContextDelegate.setFragmentFactory(factory);
	}

	/**
	 */
	@Override
	@Nullable
	public FragmentFactory getFragmentFactory() {
		this.ensureContextDelegate();
		return mContextDelegate.getFragmentFactory();
	}

	/**
	 */
	@Override
	public void setDialogController(@Nullable DialogController controller) {
		this.ensureContextDelegate();
		mContextDelegate.setDialogController(controller);
	}

	/**
	 */
	@Override
	@NonNull
	public DialogController getDialogController() {
		this.ensureContextDelegate();
		return mContextDelegate.getDialogController();
	}

	/**
	 */
	@Override
	public void setDialogXmlFactory(@XmlRes int xmlDialogsSet) {
		this.ensureContextDelegate();
		mContextDelegate.setDialogXmlFactory(xmlDialogsSet);
	}

	/**
	 */
	@Override
	public void setDialogFactory(@Nullable DialogController.DialogFactory factory) {
		this.ensureContextDelegate();
		mContextDelegate.setDialogFactory(factory);
	}

	/**
	 */
	@Override
	@Nullable
	public DialogController.DialogFactory getDialogFactory() {
		this.ensureContextDelegate();
		return mContextDelegate.getDialogFactory();
	}

	/**
	 * Requests performing of data binding specific for this activity via {@link #onBindData()}.
	 * If this activity has its view hierarchy already created {@link #onBindData()} will be invoked
	 * immediately, otherwise will wait until {@link #onContentChanged()} is invoked.
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
	 * Performs data binding of this activity. Will invoke {@link #onBindData()} if view hierarchy of
	 * this activity is already created, otherwise will register a binding request via {@link UniversiContextDelegate#registerRequest(int)}.
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
	 * activity instance.
	 * <p>
	 * <b>This is always invoked on the UI thread.</b>
	 */
	@UiThread
	protected void onBindData() {
	}

	/**
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.ensureContextDelegate();
		mContextDelegate.setPaused(false);
	}

	/**
	 */
	@CheckResult
	@Override
	public boolean isActiveNetworkConnected() {
		this.ensureContextDelegate();
		return mContextDelegate.isActiveNetworkConnected();
	}

	/**
	 */
	@CheckResult
	@Override
	public boolean isNetworkConnected(int networkType) {
		this.ensureContextDelegate();
		return mContextDelegate.isNetworkConnected(networkType);
	}

	/**
	 */
	@Override
	public int checkSelfPermission(@NonNull String permission) {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
				super.checkSelfPermission(permission) :
				PackageManager.PERMISSION_GRANTED;
	}

	/**
	 */
	@Override
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
	public void supportRequestPermissions(@NonNull String[] permissions, int requestCode) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(permissions, requestCode);
	}

	/**
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	/**
	 */
	@Override
	public boolean showDialogWithId(int dialogId) {
		return showDialogWithId(dialogId, null);
	}

	/**
	 */
	@Override
	public boolean showDialogWithId(@IntRange(from = 0) int dialogId, @Nullable DialogOptions options) {
		this.ensureContextDelegate();
		return mContextDelegate.showDialogWithId(dialogId, options);
	}

	/**
	 */
	@Override
	public boolean dismissDialogWithId(@IntRange(from = 0) int dialogId) {
		this.ensureContextDelegate();
		return mContextDelegate.dismissDialogWithId(dialogId);
	}

	/**
	 */
	@Override
	public boolean showXmlDialog(@XmlRes int resId) {
		return showXmlDialog(resId, null);
	}

	/**
	 */
	@Override
	public boolean showXmlDialog(@XmlRes int resId, @Nullable DialogOptions options) {
		this.ensureContextDelegate();
		return mContextDelegate.showXmlDialog(resId, options);
	}

	/**
	 */
	@Override
	public boolean dismissXmlDialog(@XmlRes int resId) {
		this.ensureContextDelegate();
		return mContextDelegate.dismissXmlDialog(resId);
	}

	/**
	 */
	@Override
	public void onBackPressed() {
		if (!onBackPress()) finishWithNavigationalTransition();
	}

	/**
	 * Invoked from {@link #onBackPressed()} to give a chance to this activity instance to consume
	 * the back press event before its parent.
	 * <p>
	 * This implementation first tries to dispatch this event to the current fragment via
	 * {@link #dispatchBackPressToCurrentFragment()} and if that method does not consume the event
	 * a stack with fragments will be tried to popped via {@link #popFragmentsBackStack()}.
	 *
	 * @return {@code True} if the back press event has been consumed here, {@code false} otherwise.
	 */
	protected boolean onBackPress() {
		this.ensureContextDelegate();
		return !mContextDelegate.isPaused() && (dispatchBackPressToCurrentFragment() || popFragmentsBackStack());
	}

	/**
	 * Pops stack with fragments of this activity.
	 *
	 * @return {@code True} if there was some fragment popped from the stack, {@code false} if there
	 * were no fragments to pop from.
	 */
	protected boolean popFragmentsBackStack() {
		this.ensureContextDelegate();
		return mContextDelegate.popFragmentsBackStack();
	}

	/**
	 * Dispatches back press event to the current fragment of this activity.
	 *
	 * @return {@code True} if there is presented some fragment that consumed the back press event,
	 * {@code false} otherwise.
	 * @see #findCurrentFragment()
	 * @see BackPressWatcher#dispatchBackPress()
	 */
	protected boolean dispatchBackPressToCurrentFragment() {
		final Fragment fragment = findCurrentFragment();
		return fragment instanceof BackPressWatcher && ((BackPressWatcher) fragment).dispatchBackPress();
	}

	/**
	 * Invoked whenever an event need to be dispatched to the current fragment that is presented
	 * within context of this activity.
	 *
	 * @return Current fragment of this activity (that is at this time visible), or {@code null} if
	 * there are no fragments presented within this activity at all.
	 */
	@Nullable
	protected Fragment findCurrentFragment() {
		this.ensureContextDelegate();
		return mContextDelegate.findCurrentFragment();
	}

	/**
	 */
	@Override
	protected void onPause() {
		super.onPause();
		this.ensureContextDelegate();
		mContextDelegate.setPaused(true);
	}

	/**
	 */
	@Override
	public boolean finishWithNavigationalTransition() {
		this.ensureContextDelegate();
		if (mContextDelegate.finishWithNavigationalTransition()) {
			return true;
		}
		supportFinishAfterTransition();
		return false;
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