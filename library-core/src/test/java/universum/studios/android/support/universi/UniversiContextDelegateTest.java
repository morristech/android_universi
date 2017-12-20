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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.hamcrest.core.Is;
import org.junit.Test;

import universum.studios.android.support.dialog.manage.DialogController;
import universum.studios.android.support.dialog.manage.DialogFactory;
import universum.studios.android.support.dialog.manage.DialogXmlFactory;
import universum.studios.android.support.test.local.RobolectricTestCase;
import universum.studios.android.support.test.local.TestActivity;
import universum.studios.android.support.test.local.TestFragment;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public final class UniversiContextDelegateTest extends RobolectricTestCase {

	private static final int XML_DIALOGS_SET_RESOURCE_ID = 1;
	private static final int XML_DIALOG_RESOURCE_ID = 2;
    
	@Test
	public void testInstantiation() {
		assertThat(new TestDelegate(mApplication).mContext, Is.<Context>is(mApplication));
	}

    @Test
	public void testCreateActivityDelegate() {
		assertThat(UniversiContextDelegate.create(mock(TestActivity.class)), is(notNullValue()));
    }

    @Test
	public void testCreateFragmentDelegate() {
	    assertThat(UniversiContextDelegate.create(mock(TestFragment.class)), is(notNullValue()));
    }

	@Test
	public void testSetGetDialogController() {
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogController mockController = mock(DialogController.class);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		delegate.setDialogFactory(mockFactory);
		delegate.setDialogController(mockController);
		assertThat(delegate.getDialogController(), is(mockController));
		verify(mockController, times(1)).setFactory(mockFactory);
		verifyNoMoreInteractions(mockController);
	}

	@Test
	public void testSetNullDialogController() {
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setDialogController(null);
		assertThat(delegate.getDialogController(), is(notNullValue()));
	}

	@Test
	public void testSetGetDialogFactory() {
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogFactory mockFactory = mock(DialogFactory.class);
		delegate.setDialogFactory(mockFactory);
		assertThat(delegate.getDialogFactory(), is(mockFactory));
	}

	@Test
	public void testSetDialogXmlFactory() {
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setDialogXmlFactory(XML_DIALOGS_SET_RESOURCE_ID);
		assertThat(delegate.getDialogFactory(), is(notNullValue()));
		assertThat(delegate.getDialogFactory(), instanceOf(DialogXmlFactory.class));
	}

	@Test
	public void testSetDialogXmlFactoryWithZeroResource() {
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setDialogFactory(mock(DialogFactory.class));
		delegate.setDialogXmlFactory(0);
		assertThat(delegate.getDialogFactory(), is(nullValue()));
	}

	@Test
	public void testShowDialogWithId() {
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final DialogFragment mockDialogFragment = mock(DialogFragment.class);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockFactory.isDialogProvided(1)).thenReturn(true);
		when(mockFactory.createDialogTag(1)).thenReturn("Dialog.TAG.1");
		when(mockFactory.createDialog(1, null)).thenReturn(mockDialogFragment);
		delegate.setDialogFactory(mockFactory);
		assertThat(delegate.showDialogWithId(1, null), is(true));
		verify(mockFactory, times(1)).isDialogProvided(1);
		verify(mockFactory, times(1)).createDialogTag(1);
		verify(mockFactory, times(1)).createDialog(1, null);
		verifyNoMoreInteractions(mockFactory);
	}

	@Test
	public void testShowDialogWithIdWhenPaused() {
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogFactory mockFactory = mock(DialogFactory.class);
		delegate.setDialogFactory(mockFactory);
		delegate.setPaused(true);
		assertThat(delegate.showDialogWithId(1, null), is(false));
		verifyZeroInteractions(mockFactory);
	}

	@Test
	public void testShowDialogWithIdWithoutDialogFactoryAttached() {
		assertThat(new TestDelegate(mApplication).showDialogWithId(1, null), is(false));
	}

	@Test
	public void testDismissDialogWithId() {
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final DialogFragment mockDialogFragment = mock(DialogFragment.class);
		when(mockFragmentManager.findFragmentByTag("Dialog.TAG.1")).thenReturn(mockDialogFragment);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockFactory.isDialogProvided(1)).thenReturn(true);
		when(mockFactory.createDialogTag(1)).thenReturn("Dialog.TAG.1");
		delegate.setDialogFactory(mockFactory);
		assertThat(delegate.dismissDialogWithId(1), is(true));
		verify(mockFactory, times(2)).isDialogProvided(1);
		verify(mockFactory, times(2)).createDialogTag(1);
		verifyNoMoreInteractions(mockFactory);
	}

	@Test
	public void testDismissDialogWithIdWhichIsNotShowing() {
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		when(mockFragmentManager.findFragmentByTag("Dialog.TAG.1")).thenReturn(null);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		final DialogFactory mockFactory = mock(DialogFactory.class);
		when(mockFactory.isDialogProvided(1)).thenReturn(true);
		when(mockFactory.createDialogTag(1)).thenReturn("Dialog.TAG.1");
		delegate.setDialogFactory(mockFactory);
		assertThat(delegate.dismissDialogWithId(1), is(false));
		verify(mockFactory, times(2)).isDialogProvided(1);
		verify(mockFactory, times(1)).createDialogTag(1);
		verifyNoMoreInteractions(mockFactory);
	}

	@Test
	public void testDismissDialogWithIdWhenPaused() {
		final UniversiContextDelegate delegate = new TestDelegate();
		final DialogFactory mockFactory = mock(DialogFactory.class);
		delegate.setDialogFactory(mockFactory);
		delegate.setPaused(true);
		assertThat(delegate.dismissDialogWithId(1), is(false));
		verifyZeroInteractions(mockFactory);
	}

	@Test
	public void testDismissDialogWithIdWithoutDialogFactoryAttached() {
		assertThat(new TestDelegate(mApplication).dismissDialogWithId(1), is(false));
	}

	@Test
	public void testShowXmlDialog() {
		// Ignored, because we would need to import whole dialogs library in order to successfully
		// perform this test.
	}

	@Test
	public void testShowXmlDialogWhenPaused() {
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setPaused(true);
		assertThat(delegate.showXmlDialog(XML_DIALOG_RESOURCE_ID, null), is(false));
	}

	@Test
	public void testDismissXmlDialog() {
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final DialogFragment mockDialogFragment = mock(DialogFragment.class);
		when(mockFragmentManager.findFragmentByTag(DialogXmlFactory.class.getName() + ".TAG." + dialogResource)).thenReturn(mockDialogFragment);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		assertThat(delegate.dismissXmlDialog(dialogResource), is(true));
	}

	@Test
	public void testDismissXmlDialogWhichIsNotShowing() {
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		when(mockFragmentManager.findFragmentByTag(DialogXmlFactory.class.getName() + ".TAG." + dialogResource)).thenReturn(null);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		assertThat(delegate.dismissXmlDialog(dialogResource), is(false));
	}

	@Test
	public void testDismissXmlDialogWhichIsNotDialogFragment() {
		final int dialogResource = XML_DIALOG_RESOURCE_ID;
		final TestActivity mockActivity = mock(TestActivity.class);
		final FragmentManager mockFragmentManager = mock(FragmentManager.class);
		final Fragment mockFragment = mock(TestFragment.class);
		when(mockFragmentManager.findFragmentByTag(DialogXmlFactory.class.getName() + ".TAG." + dialogResource)).thenReturn(mockFragment);
		when(mockActivity.getSupportFragmentManager()).thenReturn(mockFragmentManager);
		final UniversiContextDelegate delegate = new TestDelegate(mockActivity);
		assertThat(delegate.dismissXmlDialog(dialogResource), is(false));
	}

	@Test
	public void testDismissXmlDialogWhenPaused() {
		final UniversiContextDelegate delegate = new TestDelegate();
		delegate.setPaused(true);
		assertThat(delegate.dismissXmlDialog(XML_DIALOG_RESOURCE_ID), is(false));
	}

	@Test
	public void testIsActiveNetworkConnected() {
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(true);
		when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		final UniversiContextDelegate delegate = new TestDelegate(mockContext);
		assertThat(delegate.isActiveNetworkConnected(), is(true));
		assertThat(delegate.isActiveNetworkConnected(), is(true));
	}

	@Test
	public void testIsActiveNetworkConnectedWhenThereIsNone() {
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		assertThat(new TestDelegate(mockContext).isActiveNetworkConnected(), is(false));
	}

	@Test
	public void testIsActiveNetworkConnectedWhenDisconnected() {
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(false);
		when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		assertThat(new TestDelegate(mockContext).isActiveNetworkConnected(), is(false));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testIsNetworkConnected() {
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(true);
		when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		assertThat(new TestDelegate(mockContext).isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(true));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testIsNetworkConnectedWhenThereIsNone() {
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(null);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		assertThat(new TestDelegate(mockContext).isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testIsNetworkConnectedWhenDisconnected() {
		final Context mockContext = mock(Context.class);
		final ConnectivityManager mockConnectivityManager = mock(ConnectivityManager.class);
		final NetworkInfo mockNetworkInfo = mock(NetworkInfo.class);
		when(mockNetworkInfo.isConnected()).thenReturn(false);
		when(mockConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).thenReturn(mockNetworkInfo);
		when(mockContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
		when(mockContext.getApplicationContext()).thenReturn(mockContext);
		assertThat(new TestDelegate(mockContext).isNetworkConnected(ConnectivityManager.TYPE_MOBILE), is(false));
	}

	@Test
	public void testSetIsViewCreated() {
		final UniversiContextDelegate delegate = new TestDelegate(mApplication);
		delegate.setViewCreated(true);
		assertThat(delegate.isViewCreated(), is(true));
		delegate.setViewCreated(false);
		assertThat(delegate.isViewCreated(), is(false));
	}

	@Test
	public void testIsViewCreatedDefault() {
		assertThat(new TestDelegate(mApplication).isViewCreated(), is(false));
	}

	@Test
	public void testSetIsPaused() {
		final UniversiContextDelegate delegate = new TestDelegate(mApplication);
		delegate.setPaused(true);
		assertThat(delegate.isPaused(), is(true));
		delegate.setPaused(false);
		assertThat(delegate.isPaused(), is(false));
	}

	@Test
	public void testIsPausedDefault() {
		assertThat(new TestDelegate(mApplication).isPaused(), is(false));
	}

	@Test
	public void testRegisterRequest() {
		final UniversiContextDelegate delegate = new TestDelegate(mApplication);
		delegate.registerRequest(0x00000001);
		assertThat(delegate.isRequestRegistered(0x00000001), is(true));
	}

	@Test
	public void testUnregisterRequest() {
		final UniversiContextDelegate delegate = new TestDelegate(mApplication);
		delegate.registerRequest(0x00000001);
		delegate.unregisterRequest(0x00000001);
		assertThat(delegate.isRequestRegistered(0x00000001), is(false));
	}

    private static class TestDelegate extends UniversiContextDelegate {

	    private FragmentActivity activity;

	    TestDelegate() {
		    this(mock(TestActivity.class));
	    }

	    TestDelegate(FragmentActivity activity) {
		    this((Context) activity);
		    this.activity = activity;
	    }

	    TestDelegate(@NonNull Context context) {
		    super(context);
	    }

	    @NonNull
	    @Override
	    DialogController instantiateDialogController() {
		    return new DialogController(activity);
	    }
    }
}
