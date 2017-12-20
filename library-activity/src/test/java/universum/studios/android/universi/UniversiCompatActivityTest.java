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
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;

import org.junit.Test;
import org.robolectric.annotation.Config;

import universum.studios.android.dialog.DialogOptions;
import universum.studios.android.dialog.manage.DialogController;
import universum.studios.android.dialog.manage.DialogFactory;
import universum.studios.android.fragment.BackPressWatcher;
import universum.studios.android.fragment.annotation.ActionBarOptions;
import universum.studios.android.fragment.annotation.ContentView;
import universum.studios.android.fragment.annotation.FragmentAnnotations;
import universum.studios.android.fragment.annotation.handler.ActionBarFragmentAnnotationHandler;
import universum.studios.android.fragment.manage.FragmentController;
import universum.studios.android.fragment.manage.FragmentFactory;
import universum.studios.android.test.local.RobolectricTestCase;
import universum.studios.android.test.local.TestFragment;
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
public final class UniversiCompatActivityTest extends RobolectricTestCase {

	private static final int XML_DIALOGS_SET_RESOURCE_ID = 1;
	private static final int XML_DIALOG_RESOURCE_ID = 2;

	@Override
	public void beforeTest() throws Exception {
		super.beforeTest();
		// Ensure that we have always annotations processing enabled.
		FragmentAnnotations.setEnabled(true);
	}

	@Test
	public void testREQUEST_BIND_DATA_INNER() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.isViewCreated()).thenReturn(true);
		activity.REQUEST_BIND_DATA_INNER.run();
		assertThat(activity.onBindDataInvoked, is(true));
		verify(mockDelegate, times(1)).isViewCreated();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnCreateAnnotationHandler() {
		final TestActivity activity = new TestActivity();
		final ActionBarFragmentAnnotationHandler annotationHandler = activity.onCreateAnnotationHandler();
		assertThat(annotationHandler, is(not(nullValue())));
		assertThat(annotationHandler, is(activity.onCreateAnnotationHandler()));
	}

	@Test
	public void testGetAnnotationHandler() {
		assertThat(new TestActivity().getAnnotationHandler(), is(not(nullValue())));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetAnnotationHandlerWhenAnnotationsAreDisabled() {
		FragmentAnnotations.setEnabled(false);
		new TestActivity().getAnnotationHandler();
	}

	@Test
	public void testStartLoader() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final LoaderManager.LoaderCallbacks mockLoaderCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.startLoader(1, null, mockLoaderCallbacks);
		verify(mockDelegate, times(1)).startLoader(eq(1), (Bundle) isNull(), eq(mockLoaderCallbacks));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testInitLoader() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final LoaderManager.LoaderCallbacks mockLoaderCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.initLoader(1, null, mockLoaderCallbacks);
		verify(mockDelegate, times(1)).initLoader(eq(1), (Bundle) isNull(), eq(mockLoaderCallbacks));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testRestartLoader() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final LoaderManager.LoaderCallbacks mockLoaderCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.restartLoader(1, null, mockLoaderCallbacks);
		verify(mockDelegate, times(1)).restartLoader(eq(1), (Bundle) isNull(), eq(mockLoaderCallbacks));
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testDestroyLoader() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.destroyLoader(1);
		verify(mockDelegate, times(1)).destroyLoader(1);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnCreateOptionsMenuWithDisabledAnnotations() {
		FragmentAnnotations.setEnabled(false);
		final TestActivity activity = new TestActivity();
		final Menu mockMenu = mock(Menu.class);
		activity.onCreateOptionsMenu(mockMenu);
		verifyZeroInteractions(mockMenu);
	}

	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testSetGetNavigationalTransition() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final BaseNavigationalTransition mockNavigationalTransition = mock(BaseNavigationalTransition.class);
		when(mockDelegate.getNavigationalTransition()).thenReturn(mockNavigationalTransition);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.setNavigationalTransition(mockNavigationalTransition);
		verify(mockDelegate, times(1)).setNavigationalTransition(mockNavigationalTransition);
		assertThat(activity.getNavigationalTransition(), is(mockNavigationalTransition));
		verify(mockDelegate, times(1)).getNavigationalTransition();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testSetGetFragmentController() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final FragmentController mockController = mock(FragmentController.class);
		when(mockDelegate.getFragmentController()).thenReturn(mockController);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.setFragmentController(mockController);
		verify(mockDelegate, times(1)).setFragmentController(mockController);
		assertThat(activity.getFragmentController(), is(mockController));
		verify(mockDelegate, times(1)).getFragmentController();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testSetGetFragmentFactory() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final FragmentFactory mockFactory = mock(FragmentFactory.class);
		when(mockDelegate.getFragmentFactory()).thenReturn(mockFactory);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.setFragmentFactory(mockFactory);
		verify(mockDelegate, times(1)).setFragmentFactory(mockFactory);
		assertThat(activity.getFragmentFactory(), is(mockFactory));
		verify(mockDelegate, times(1)).getFragmentFactory();
	}

	@Test
	public void testSetGetDialogController() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final DialogController mockController = mock(DialogController.class);
		when(mockDelegate.getDialogController()).thenReturn(mockController);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.setDialogController(mockController);
		verify(mockDelegate, times(1)).setDialogController(mockController);
		assertThat(activity.getDialogController(), is(mockController));
		verify(mockDelegate, times(1)).getDialogController();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testGetDialogControllerDefault() {
		assertThat(new TestActivity().getDialogController(), is(notNullValue()));
	}

	@Test
	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void testSetGetDialogFactory() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockDelegate.getDialogFactory()).thenReturn(mockFactory);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.setDialogFactory(mockFactory);
		verify(mockDelegate, times(1)).setDialogFactory(mockFactory);
		assertThat(activity.getDialogFactory(), is(mockFactory));
		verify(mockDelegate, times(1)).getDialogFactory();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testSetDialogXmlFactory() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final int dialogsResource = XML_DIALOGS_SET_RESOURCE_ID;
		activity.setDialogXmlFactory(dialogsResource);
		verify(mockDelegate, times(1)).setDialogXmlFactory(dialogsResource);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnContentChanged() {
	}

	@Test
	public void testOnContentChangedWithRequestToBindData() {
	}

	@Test
	public void testOnResume() {
	}

	@Test
	public void testRequestBindData() throws Throwable {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.isViewCreated()).thenReturn(false);
		activity.requestBindData();
		assertThat(activity.onBindDataInvoked, is(false));
		verify(mockDelegate, times(1)).isViewCreated();
		verify(mockDelegate, times(1)).registerRequest(UniversiContextDelegate.REQUEST_BIND_DATA);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testRequestBindDataWhenViewIsCreated() throws Throwable {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.isViewCreated()).thenReturn(true);
		activity.requestBindData();
		assertThat(activity.onBindDataInvoked, is(true));
		verify(mockDelegate, times(1)).isViewCreated();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testIsActiveNetworkConnected() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		assertThat(activity.isActiveNetworkConnected(), is(false));
		verify(mockDelegate, times(1)).isActiveNetworkConnected();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testIsNetworkConnected() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		assertThat(activity.isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
		verify(mockDelegate, times(1)).isNetworkConnected(ConnectivityManager.TYPE_MOBILE);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testSupportRequestPermissions() {
	}

	@Test
	@Config(sdk = Build.VERSION_CODES.M)
	public void testOnRequestPermissionsResult() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.onRequestPermissionsResult(1, new String[0], new int[0]);
		verifyZeroInteractions(mockDelegate);
	}

	@Test
	public void testShowDialogWithId() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.showDialogWithId(1);
		verify(mockDelegate, times(1)).showDialogWithId(eq(1), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testShowDialogWithIdAndOptions() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final DialogOptions mockOptions = mock(DialogOptions.class);
		activity.showDialogWithId(1, mockOptions);
		verify(mockDelegate, times(1)).showDialogWithId(1, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testDismissDialogWithId() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		activity.dismissDialogWithId(1);
		verify(mockDelegate, times(1)).dismissDialogWithId(1);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testShowXmlDialog() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		activity.showXmlDialog(dialogResource);
		verify(mockDelegate, times(1)).showXmlDialog(eq(dialogResource), (DialogOptions) isNull());
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testShowXmlDialogWithOptions() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final DialogOptions mockOptions = mock(DialogOptions.class);
		activity.showXmlDialog(dialogResource, mockOptions);
		verify(mockDelegate, times(1)).showXmlDialog(dialogResource, mockOptions);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testDismissXmlDialog() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		activity.dismissXmlDialog(dialogResource);
		verify(mockDelegate, times(1)).dismissXmlDialog(dialogResource);
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnBackPressed() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		when(mockDelegate.finishWithNavigationalTransition()).thenReturn(true);
		activity.onBackPressed();
		verify(mockDelegate, times(1)).isPaused();
		verify(mockDelegate, times(1)).findCurrentFragment();
		verify(mockDelegate, times(1)).popFragmentsBackStack();
		verify(mockDelegate, times(1)).finishWithNavigationalTransition();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnBackPressedWithFragmentsInBackStack() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		when(mockDelegate.popFragmentsBackStack()).thenReturn(true);
		activity.onBackPressed();
		verify(mockDelegate, times(1)).isPaused();
		verify(mockDelegate, times(1)).findCurrentFragment();
		verify(mockDelegate, times(1)).popFragmentsBackStack();
		verify(mockDelegate, times(0)).finishWithNavigationalTransition();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnBackPress() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		assertThat(activity.onBackPress(), is(false));
		verify(mockDelegate, times(1)).isPaused();
		verify(mockDelegate, times(1)).findCurrentFragment();
		verify(mockDelegate, times(1)).popFragmentsBackStack();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testOnBackPressHandledByCurrentFragment() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.onBackPress(), is(true));
		verify(mockDelegate, times(1)).isPaused();
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment, times(1)).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test
	public void testOnBackPressNotHandledByCurrentFragment() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(false);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.onBackPress(), is(false));
		verify(mockDelegate, times(1)).isPaused();
		verify(mockDelegate, times(1)).findCurrentFragment();
		verify(mockDelegate, times(1)).popFragmentsBackStack();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment, times(1)).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test
	public void testOnBackPressWhenPaused() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.isPaused()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.onBackPress(), is(false));
		verify(mockDelegate, times(1)).isPaused();
		verifyNoMoreInteractions(mockDelegate);
		verifyZeroInteractions(mockFragment);
	}

	@Test
	public void testDispatchBackPressToFragments() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.dispatchBackPressToFragments(), is(true));
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment, times(1)).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test
	public void testDispatchBackPressToCurrentFragment() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(true);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(true));
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment, times(1)).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test
	public void testDispatchBackPressToCurrentFragmentNotHandledByFragment() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestBackPressWatcherFragment mockFragment = mock(TestBackPressWatcherFragment.class);
		when(mockFragment.dispatchBackPress()).thenReturn(false);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(false));
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
		verify(mockFragment, times(1)).dispatchBackPress();
		verifyNoMoreInteractions(mockFragment);
	}

	@Test
	public void testDispatchBackPressToCurrentFragmentThatIsNotBackPressWatcher() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final TestFragment mockFragment = mock(TestFragment.class);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(false));
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testDispatchBackPressToCurrentFragmentWhenThereIsNone() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.findCurrentFragment()).thenReturn(null);
		assertThat(activity.dispatchBackPressToCurrentFragment(), is(false));
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testFindCurrentFragment() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		final Fragment mockFragment = mock(TestFragment.class);
		when(mockDelegate.findCurrentFragment()).thenReturn(mockFragment);
		assertThat(activity.findCurrentFragment(), is(mockFragment));
		verify(mockDelegate, times(1)).findCurrentFragment();
		verifyNoMoreInteractions(mockDelegate);
	}

	@Test
	public void testPopFragmentsBackStack() {
		final UniversiActivityDelegate mockDelegate = mock(UniversiActivityDelegate.class);
		final TestActivity activity = new TestActivity();
		activity.setContextDelegate(mockDelegate);
		when(mockDelegate.popFragmentsBackStack()).thenReturn(true);
		assertThat(activity.popFragmentsBackStack(), is(true));
		verify(mockDelegate, times(1)).popFragmentsBackStack();
		verifyNoMoreInteractions(mockDelegate);
	}

	@ActionBarOptions(
			homeAsUp = ActionBarOptions.HOME_AS_UP_ENABLED,
			homeAsUpIndicator = android.R.drawable.ic_delete
	)
	@ContentView(android.R.layout.simple_list_item_1)
	public static final class TestActivity extends UniversiCompatActivity {

		boolean onBindViewsInvoked, onBindDataInvoked;

		@Override
		protected void onBindViews() {
			super.onBindViews();
			this.onBindViewsInvoked = true;
		}

		@Override
		protected void onBindData() {
			super.onBindData();
			this.onBindDataInvoked = true;
		}
	}

	public static abstract class TestBackPressWatcherFragment extends TestFragment implements BackPressWatcher {
	}
}
