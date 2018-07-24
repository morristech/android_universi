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
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.view.Menu;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import universum.studios.android.support.dialog.DialogOptions;
import universum.studios.android.support.dialog.manage.DialogController;
import universum.studios.android.support.dialog.manage.DialogFactory;
import universum.studios.android.support.fragment.BackPressWatcher;
import universum.studios.android.support.fragment.annotation.ActionBarOptions;
import universum.studios.android.support.fragment.annotation.ContentView;
import universum.studios.android.support.fragment.annotation.FragmentAnnotations;
import universum.studios.android.support.fragment.annotation.handler.ActionBarFragmentAnnotationHandler;
import universum.studios.android.support.fragment.manage.FragmentController;
import universum.studios.android.support.fragment.manage.FragmentFactory;
import universum.studios.android.support.test.local.RobolectricTestCase;
import universum.studios.android.support.test.local.TestFragment;
import universum.studios.android.transition.BaseNavigationalTransition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public final class UniversiActivityTest extends RobolectricTestCase {

	private static final int XML_DIALOGS_SET_RESOURCE_ID = 1;
	private static final int XML_DIALOG_RESOURCE_ID = 2;

	@Override public void beforeTest() throws Exception {
		super.beforeTest();
		// Ensure that we have always annotations processing enabled.
		FragmentAnnotations.setEnabled(true);
	}

	@Test public void testREQUEST_BIND_DATA_INNER() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.isViewCreated()).thenReturn(true);
		// Act:
		activity.REQUEST_BIND_DATA_INNER.run();
		// Assert:
		assertThat(activity.onBindDataInvoked, is(true));
		verify(mockDelegate).isViewCreated();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testInstantiation() {
		// Act:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().get();
		// Assert:
		assertThat(activity.getFragmentController(), is(notNullValue()));
		assertThat(activity.getDialogController(), is(notNullValue()));
	}

	@Test public void testOnCreateAnnotationHandler() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		// Act:
		final ActionBarFragmentAnnotationHandler annotationHandler = activity.onCreateAnnotationHandler();
		// Assert:
		assertThat(annotationHandler, is(notNullValue()));
		assertThat(annotationHandler, is(activity.onCreateAnnotationHandler()));
	}

	@Test public void testAnnotationHandler() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		// Act + Assert:
		assertThat(activity.getAnnotationHandler(), is(not(nullValue())));
	}

	@Test(expected = IllegalStateException.class)
	public void testAnnotationHandlerWhenAnnotationsAreDisabled() {
		// Arrange:
		FragmentAnnotations.setEnabled(false);
		final TestActivity activity = new TestActivity();
		// Act:
		activity.getAnnotationHandler();
	}

	@Test public void testStartLoader() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final LoaderManager.LoaderCallbacks mockLoaderCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.startLoader(1, null, mockLoaderCallbacks);
		// Assert:
		verify(mockDelegate).startLoader(eq(1), (Bundle) isNull(), eq(mockLoaderCallbacks));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testInitLoader() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final LoaderManager.LoaderCallbacks mockLoaderCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.initLoader(1, null, mockLoaderCallbacks);
		// Assert:
		verify(mockDelegate).initLoader(eq(1), (Bundle) isNull(), eq(mockLoaderCallbacks));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testRestartLoader() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final LoaderManager.LoaderCallbacks mockLoaderCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.restartLoader(1, null, mockLoaderCallbacks);
		// Assert:
		verify(mockDelegate).restartLoader(eq(1), (Bundle) isNull(), eq(mockLoaderCallbacks));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDestroyLoader() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.destroyLoader(1);
		// Assert:
		verify(mockDelegate).destroyLoader(1);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnCreateOptionsMenu() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		final Menu mockMenu = mock(Menu.class);
		// Act:
		activity.onCreateOptionsMenu(mockMenu);
		// Assert:
		verifyZeroInteractions(mockMenu);
	}

	@Test public void testOnCreateOptionsMenuWithDisabledAnnotations() {
		// Arrange:
		FragmentAnnotations.setEnabled(false);
		final TestActivity activity = new TestActivity();
		final Menu mockMenu = mock(Menu.class);
		// Act:
		activity.onCreateOptionsMenu(mockMenu);
		// Assert:
		verifyZeroInteractions(mockMenu);
	}

	@Test public void testNavigationalTransition() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final BaseNavigationalTransition mockNavigationalTransition = mock(BaseNavigationalTransition.class);
		when(mockDelegate.getNavigationalTransition()).thenReturn(mockNavigationalTransition);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		activity.setNavigationalTransition(mockNavigationalTransition);
		verify(mockDelegate).setNavigationalTransition(mockNavigationalTransition);
		assertThat(activity.getNavigationalTransition(), is(mockNavigationalTransition));
		verify(mockDelegate).getNavigationalTransition();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testFragmentController() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final FragmentController mockController = mock(FragmentController.class);
		when(mockDelegate.getFragmentController()).thenReturn(mockController);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		activity.setFragmentController(mockController);
		verify(mockDelegate).setFragmentController(mockController);
		assertThat(activity.getFragmentController(), is(mockController));
		verify(mockDelegate).getFragmentController();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testFragmentFactory() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final FragmentFactory mockFactory = mock(FragmentFactory.class);
		when(mockDelegate.getFragmentFactory()).thenReturn(mockFactory);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		activity.setFragmentFactory(mockFactory);
		verify(mockDelegate).setFragmentFactory(mockFactory);
		assertThat(activity.getFragmentFactory(), is(mockFactory));
		verify(mockDelegate, times(1)).getFragmentFactory();
	}

	@Test public void testDialogController() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final DialogController mockController = mock(DialogController.class);
		when(mockDelegate.getDialogController()).thenReturn(mockController);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		activity.setDialogController(mockController);
		verify(mockDelegate, times(1)).setDialogController(mockController);
		assertThat(activity.getDialogController(), is(mockController));
		verify(mockDelegate, times(1)).getDialogController();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDialogFactory() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockDelegate.getDialogFactory()).thenReturn(mockFactory);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		activity.setDialogFactory(mockFactory);
		verify(mockDelegate).setDialogFactory(mockFactory);
		assertThat(activity.getDialogFactory(), is(mockFactory));
		verify(mockDelegate).getDialogFactory();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDialogXmlFactory() {
		// Arrange:
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final int dialogsResource = XML_DIALOGS_SET_RESOURCE_ID;
		// Act + Assert:
		activity.setDialogXmlFactory(dialogsResource);
		verify(mockDelegate).setDialogXmlFactory(dialogsResource);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnContentChanged() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().get();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.onContentChanged();
		// Assert:
		assertThat(activity.onBindViewsInvoked, is(true));
		verify(mockDelegate).setViewCreated(true);
		verify(mockDelegate).isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnContentChangedWithRequestToBindData() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().get();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		when(mockDelegate.isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA)).thenReturn(true);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.onContentChanged();
		// Assert:
		assertThat(activity.onBindViewsInvoked, is(true));
		assertThat(activity.onBindDataInvoked, is(true));
		verify(mockDelegate).setViewCreated(true);
		verify(mockDelegate).isRequestRegistered(UniversiContextDelegate.REQUEST_BIND_DATA);
		verify(mockDelegate).unregisterRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnResume() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().get();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.onResume();
		// Assert:
		verify(mockDelegate).setStateSaved(false);
		verify(mockDelegate).setPaused(false);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testRequestBindData() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		when(mockDelegate.isViewCreated()).thenReturn(false);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.requestBindData();
		// Assert:
		assertThat(activity.onBindDataInvoked, is(false));
		verify(mockDelegate).isViewCreated();
		verify(mockDelegate).registerRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testRequestBindDataWhenViewIsCreated() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		when(mockDelegate.isViewCreated()).thenReturn(true);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.requestBindData();
		// Assert:
		assertThat(activity.onBindDataInvoked, is(true));
		verify(mockDelegate).isViewCreated();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testRequestBindDataFromBackgroundThread() throws Throwable {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		// Act:
		activity.requestBindData();
		// Assert:
		Thread.sleep(200);
		assertThat(activity.onBindDataInvoked, is(true));
	}

	@Test public void testIsActiveNetworkConnected() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		assertThat(activity.isActiveNetworkConnected(), is(false));
		verify(mockDelegate).isActiveNetworkConnected();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testIsNetworkConnected() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act + Assert:
		assertThat(activity.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
		verify(mockDelegate).isNetworkConnected(ConnectivityManager.TYPE_MOBILE);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testCheckSelfPermission() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		// Act + Assert:
		assertThat(
				activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE),
				is(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
						application.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Process.myPid(), Process.myUid()) :
						PackageManager.PERMISSION_GRANTED
				)
		);
	}

	@Test public void testShouldShowRequestPermissionRationale() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		// Act + Assert:
		assertThat(activity.shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE), is(false));
	}

	@Test public void testSupportRequestPermissions() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.supportRequestPermissions(new String[0], 1);
		// Assert:
		verifyZeroInteractions(mockDelegate);
	}

	@Config(sdk = Build.VERSION_CODES.M)
	@Test public void testOnRequestPermissionsResult() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.onRequestPermissionsResult(1, new String[0], new int[0]);
		// Assert:
		verifyZeroInteractions(mockDelegate);
	}

	@Test public void testShowDialogWithId() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.showDialogWithId(1);
		// Assert:
		verify(mockDelegate).showDialogWithId(eq(1), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testShowDialogWithIdAndOptions() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final DialogOptions mockOptions = mock(DialogOptions.class);
		// Act:
		activity.showDialogWithId(1, mockOptions);
		// Assert:
		verify(mockDelegate).showDialogWithId(1, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDismissDialogWithId() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.dismissDialogWithId(1);
		// Assert:
		verify(mockDelegate).dismissDialogWithId(1);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testShowXmlDialog() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		// Act:
		activity.showXmlDialog(dialogResource);
		// Assert:
		verify(mockDelegate).showXmlDialog(eq(dialogResource), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testShowXmlDialogWithOptions() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final DialogOptions mockOptions = mock(DialogOptions.class);
		// Act:
		activity.showXmlDialog(dialogResource, mockOptions);
		// Assert:
		verify(mockDelegate).showXmlDialog(dialogResource, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDismissXmlDialog() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		// Act:
		activity.dismissXmlDialog(dialogResource);
		// Assert:
		verify(mockDelegate).dismissXmlDialog(dialogResource);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnBackPressed() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		when(mockDelegate.finishWithNavigationalTransition()).thenReturn(true);
		// Act:
		activity.onBackPressed();
		// Assert:
		verify(mockDelegate).isPaused();
		verify(mockDelegate).findCurrentFragment();
		verify(mockDelegate).popFragmentsBackStack();
		verify(mockDelegate).finishWithNavigationalTransition();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnBackPressedWithFragmentsInBackStack() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		when(mockDelegate.popFragmentsBackStack()).thenReturn(true);
		// Act:
		activity.onBackPressed();
		// Assert:
		verify(mockDelegate).isPaused();
		verify(mockDelegate).findCurrentFragment();
		verify(mockDelegate).popFragmentsBackStack();
		verify(mockDelegate, times(0)).finishWithNavigationalTransition();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnBackPress() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		// Act + Assert:
		assertThat(activity.onBackPress(), is(false));
		verify(mockDelegate).isPaused();
		verify(mockDelegate).findCurrentFragment();
		verify(mockDelegate).popFragmentsBackStack();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnBackPressHandledByCurrentFragment() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.onBackPress(), is(true));
		verify(mockDelegate).isPaused();
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test public void testOnBackPressNotHandledByCurrentFragment() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(false);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.onBackPress(), is(false));
		verify(mockDelegate).isPaused();
		verify(mockDelegate).findCurrentFragment();
		verify(mockDelegate).popFragmentsBackStack();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test public void testOnBackPressWhenPaused() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.isPaused()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.onBackPress(), is(false));
		verify(mockDelegate).isPaused();
		verifyNoMoreInteractions(mockDelegate);
		verifyZeroInteractions(mockFragment);
	}

	@Test public void testDispatchBackPressToFragments() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.dispatchBackPressToFragments(), is(true));
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test public void testDispatchBackPressToCurrentFragment() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(true));
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test public void testDispatchBackPressToCurrentFragmentNotHandledByFragment() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(false);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(false));
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test public void testDispatchBackPressToCurrentFragmentThatIsNotBackPressWatcher() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final TestFragment mockFragment = mock(TestFragment.class);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(false));
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testDispatchBackPressToCurrentFragmentWhenThereIsNone() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		// Act + Assert:
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(false));
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testFindCurrentFragment() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final Fragment mockFragment = mock(TestFragment.class);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		// Act + Assert:
		assertThat(activity.findCurrentFragment(), is(mockFragment));
		verify(mockDelegate).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testPopFragmentsBackStack() {
		// Arrange:
		final TestActivity activity = new TestActivity();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.popFragmentsBackStack()).thenReturn(true);
		// Act + Assert:
		assertThat(activity.popFragmentsBackStack(), is(true));
		verify(mockDelegate).popFragmentsBackStack();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testOnSaveInstanceState() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final Bundle savedState = new Bundle();
		// Act:
		activity.onSaveInstanceState(savedState);
		// Assert:
		verify(mockDelegate).setStateSaved(true);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Config(sdk = Build.VERSION_CODES.LOLLIPOP)
	@Test public void testOnSaveInstanceStatePersistable() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		final Bundle savedState = new Bundle();
		final PersistableBundle savedStatePersistable = new PersistableBundle();
		// Act:
		activity.onSaveInstanceState(savedState, savedStatePersistable);
		// Assert:
		verify(mockDelegate, times(2)).setStateSaved(true);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testPause() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		activity.setContextDelegate(mockDelegate);
		// Act:
		activity.onPause();
		// Assert:
		verify(mockDelegate).setPaused(true);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test public void testFinishWithNavigationalTransition() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		activity.setNavigationalTransition(new BaseNavigationalTransition() {});
		// Act + Assert:
		assertThat(activity.finishWithNavigationalTransition(), is(true));
		assertThat(activity.isFinishing(), is(true));
	}

	@Test public void testFinishWithNavigationalTransitionWhenThereIsNoTransition() {
		// Arrange:
		final TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().start().resume().get();
		// Act + Assert:
		assertThat(activity.finishWithNavigationalTransition(), is(false));
		assertThat(activity.isFinishing(), is(true));
	}

	@ActionBarOptions(
			homeAsUp = ActionBarOptions.HOME_AS_UP_ENABLED,
			homeAsUpIndicator = android.R.drawable.ic_delete
	)
	@ContentView(android.R.layout.simple_list_item_1)
	public static final class TestActivity extends UniversiActivity {

		boolean onBindViewsInvoked, onBindDataInvoked;

		@Override protected void onBindViews() {
			super.onBindViews();
			this.onBindViewsInvoked = true;
		}

		@Override protected void onBindData() {
			super.onBindData();
			this.onBindDataInvoked = true;
		}
	}

	public static abstract class TestBackPressWatcherFragment extends TestFragment implements BackPressWatcher {}
}