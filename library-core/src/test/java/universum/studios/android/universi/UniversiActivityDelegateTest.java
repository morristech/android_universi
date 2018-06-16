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

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;

import org.hamcrest.core.Is;
import org.junit.Test;

import universum.studios.android.fragment.manage.FragmentController;
import universum.studios.android.fragment.manage.FragmentFactory;
import universum.studios.android.test.local.RobolectricTestCase;
import universum.studios.android.test.local.TestActivity;
import universum.studios.android.test.local.TestFragment;
import universum.studios.android.transition.BaseNavigationalTransition;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * @author Martin Albedinsky
 */
public final class UniversiActivityDelegateTest extends RobolectricTestCase {

	private Activity mMockActivity;

	@Override
	public void beforeTest() throws Exception {
		super.beforeTest();
		this.mMockActivity = mock(TestActivity.class);
	}

	@Test
	public void testInstantiation() {
		assertThat(new UniversiActivityDelegate(mMockActivity).context, Is.<Context>is(mMockActivity));
	}

	@Test
	public void testInstantiateDialogController() {
		assertThat(new UniversiActivityDelegate(mMockActivity).instantiateDialogController(), is(notNullValue()));
	}

	@Test
	public void testStartLoader() {
		final Loader mockLoader = mock(Loader.class);
		final LoaderManager.LoaderCallbacks mockCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final LoaderManager mockLoaderManager = mock(LoaderManager.class);
		when(mockLoaderManager.initLoader(1, null, mockCallbacks)).thenReturn(mockLoader);
		when(mockLoaderManager.getLoader(1)).thenReturn(null);
		when(mMockActivity.getLoaderManager()).thenReturn(mockLoaderManager);
		when(mockCallbacks.onCreateLoader(1, null)).thenReturn(mockLoader);
		assertThat(new UniversiActivityDelegate(mMockActivity).startLoader(1, null, mockCallbacks), is(mockLoader));
		verify(mockLoaderManager, times(1)).getLoader(1);
		verify(mockLoaderManager, times(1)).initLoader(1, null, mockCallbacks);
		verifyNoMoreInteractions(mockLoaderManager);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testStartLoaderWhenAlreadyStarted() {
		final Loader mockLoader = mock(Loader.class);
		final LoaderManager.LoaderCallbacks mockCallbacks = mock(LoaderManager.LoaderCallbacks.class);
		final LoaderManager mockLoaderManager = mock(LoaderManager.class);
		when(mockLoaderManager.restartLoader(1, null, mockCallbacks)).thenReturn(mockLoader);
		when(mockLoaderManager.getLoader(1)).thenReturn(mockLoader);
		when(mMockActivity.getLoaderManager()).thenReturn(mockLoaderManager);
		assertThat(new UniversiActivityDelegate(mMockActivity).startLoader(1, null, mockCallbacks), is(mockLoader));
		verify(mockLoaderManager, times(1)).getLoader(1);
		verify(mockLoaderManager, times(1)).restartLoader(1, null, mockCallbacks);
		verifyNoMoreInteractions(mockLoaderManager);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDestroyLoader() {
		final LoaderManager mockLoaderManager = mock(LoaderManager.class);
		when(mMockActivity.getLoaderManager()).thenReturn(mockLoaderManager);
		new UniversiActivityDelegate(mMockActivity).destroyLoader(1);
		verify(mockLoaderManager, times(1)).destroyLoader(1);
		verifyNoMoreInteractions(mockLoaderManager);
	}

	@Test
	public void testSetGetNavigationalTransition() {
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		final BaseNavigationalTransition mockTransition = mock(BaseNavigationalTransition.class);
		delegate.setNavigationalTransition(mockTransition);
		assertThat(delegate.getNavigationalTransition(), is(mockTransition));
		verify(mockTransition, times(1)).configureIncomingTransitions(mMockActivity);
	}

	@Test
	public void testSetNullNavigationalTransition() {
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		delegate.setNavigationalTransition(null);
		assertThat(delegate.getNavigationalTransition(), is(nullValue()));
	}

	@Test
	public void testFinishWithNavigationalTransition() {
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		final BaseNavigationalTransition mockTransition = mock(BaseNavigationalTransition.class);
		delegate.setNavigationalTransition(mockTransition);
		assertThat(delegate.finishWithNavigationalTransition(), is(true));
	}

	@Test
	public void testFinishWithNavigationalTransitionWithoutAttachedTransition() {
		assertThat(new UniversiActivityDelegate(mMockActivity).finishWithNavigationalTransition(), is(false));
	}

	@Test
	public void testSetGetFragmentController() {
		final FragmentFactory mockFactory = mock(FragmentFactory.class);
		final FragmentController mockController = mock(FragmentController.class);
		when(mMockActivity.getFragmentManager()).thenReturn(mock(FragmentManager.class));
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		delegate.setFragmentFactory(mockFactory);
		delegate.setFragmentController(mockController);
		assertThat(delegate.getFragmentController(), is(mockController));
		verify(mockController, times(1)).setFactory(mockFactory);
		verifyNoMoreInteractions(mockController);
	}

	@Test
	public void testSetNullFragmentController() {
		when(mMockActivity.getFragmentManager()).thenReturn(mock(FragmentManager.class));
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		delegate.setFragmentController(null);
		assertThat(delegate.getFragmentController(), is(notNullValue()));
	}

	@Test
	public void testSetGetFragmentFactory() {
		when(mMockActivity.getFragmentManager()).thenReturn(mock(FragmentManager.class));
		final FragmentFactory mockFactory = mock(FragmentFactory.class);
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		delegate.setFragmentFactory(mockFactory);
		assertThat(delegate.getFragmentFactory(), is(mockFactory));
		verifyZeroInteractions(mockFactory);
	}

	@Test
	public void testFindCurrentFragment() {
		final UniversiActivityDelegate delegate = new UniversiActivityDelegate(mMockActivity);
		final FragmentController mockController = mock(FragmentController.class);
		final Fragment mockFragment = mock(TestFragment.class);
		when(mockController.findCurrentFragment()).thenReturn(mockFragment);
		delegate.setFragmentController(mockController);
		assertThat(delegate.findCurrentFragment(), is(mockFragment));
	}

	@Test
	public void testFindCurrentFragmentWithoutControllerInitialized() {
		final FragmentManager mockManager = mock(FragmentManager.class);
		when(mockManager.getBackStackEntryCount()).thenReturn(1);
		when(mMockActivity.getFragmentManager()).thenReturn(mockManager);
		assertThat(new UniversiActivityDelegate(mMockActivity).findCurrentFragment(), is(nullValue()));
	}

	@Test
	public void testPopFragmentsBackStack() {
		final FragmentManager mockManager = mock(FragmentManager.class);
		when(mockManager.getBackStackEntryCount()).thenReturn(1);
		when(mMockActivity.getFragmentManager()).thenReturn(mockManager);
		assertThat(new UniversiActivityDelegate(mMockActivity).popFragmentsBackStack(), is(true));
		verify(mockManager, times(1)).getBackStackEntryCount();
		verify(mockManager, times(1)).popBackStack();
		verifyNoMoreInteractions(mockManager);
	}

	@Test
	public void testPopFragmentsBackStackOnEmptyStack() {
		final FragmentManager mockManager = mock(FragmentManager.class);
		when(mockManager.getBackStackEntryCount()).thenReturn(0);
		when(mMockActivity.getFragmentManager()).thenReturn(mockManager);
		assertThat(new UniversiActivityDelegate(mMockActivity).popFragmentsBackStack(), is(false));
		verify(mockManager, times(1)).getBackStackEntryCount();
		verifyNoMoreInteractions(mockManager);
	}
}