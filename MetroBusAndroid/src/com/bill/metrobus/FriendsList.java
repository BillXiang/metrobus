/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bill.metrobus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bill.metrobus.QuickAction.ActionItem;
import com.bill.metrobus.QuickAction.QuickAction;
import com.bill.metrobus.bluetoothchat.BluetoothChatActivity;
import com.bill.metrobus.netchat.NetChatActivity;
import com.bill.metrobus.util.HttpClientHelper;
import com.tencent.tencentmap.mapsdk.search.BusItem;
import com.tencent.tencentmap.mapsdk.search.BusSearch;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.support.v4.widget.SimpleCursorAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.Contacts.People;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

/**
 * Demonstration of the use of a CursorLoader to load and display contacts data
 * in a fragment.
 */
@SuppressWarnings("all")
public class FriendsList extends ListFragment implements
		LoaderManager.LoaderCallbacks<List<UserEntity>> {

	private int mSelectedRow = 0;
	
	// This is the Adapter being used to display the list's data.
	private FriendListAdapter adapter;

	// If non-null, this is the current filter the user has provided.
	private String mCurFilter;
	
	private Context context;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		context = getActivity();
		
		// Give some text to display if there is no data. In a real
		// application this would come from a resource.
		setEmptyText("没有在线用户");

		// We have a menu item to show in action bar.
		setHasOptionsMenu(true);

		// Create an empty adapter we will use to display the loaded data.
		adapter = new FriendListAdapter(getActivity());
		setListAdapter(adapter);

		// Start out with a progress indicator.
		setListShown(false);

		getLoaderManager().initLoader(0, null, this);

		
		ActionItem btChatItem = new ActionItem(0, "蓝牙聊天", null);
		ActionItem netChatItem = new ActionItem(1, "网络聊天", null);
		
		final QuickAction mQuickAction = new QuickAction(getActivity());
		
		mQuickAction.addActionItem(btChatItem);
		mQuickAction.addActionItem(netChatItem);
		
		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			public void onItemClick(QuickAction quickAction, int pos, int actionId) {
				ActionItem actionItem = quickAction.getActionItem(pos);
				if (actionId == 0) { //btchat item selected
					Intent intent = new Intent(context,
							BluetoothChatActivity.class);
					startActivity(intent);
				} else if(actionId==1){//netchat
					Intent intent = new Intent(context,
							NetChatActivity.class);
					startActivity(intent);
				}	
			}
		});
		
		//setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {			
			public void onDismiss() {
				//mMoreIv.setImageResource(R.drawable.coursetable_ic_list_more);
			}
		});
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				if(id<-1){
//					return ;
//				}
				//TODO
			}
		});
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				mSelectedRow = position; //set the selected row
				
				mQuickAction.show(view);
				return true;
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// Place an action bar item for searching.
		MenuItem item = menu.add("Search");
		item.setIcon(android.R.drawable.ic_menu_search);
		MenuItemCompat.setShowAsAction(item,
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS
						| MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		final View searchView = SearchViewCompat.newSearchView(getActivity());
		if (searchView != null) {
			SearchViewCompat.setOnQueryTextListener(searchView,
					new OnQueryTextListenerCompat() {
						@Override
						public boolean onQueryTextChange(String newText) {
							// Called when the action bar search text has
							// changed. Update
							// the search filter, and restart the loader to do a
							// new query
							// with this filter.
							String newFilter = !TextUtils.isEmpty(newText) ? newText
									: null;
							// Don't do anything if the filter hasn't actually
							// changed.
							// Prevents restarting the loader when restoring
							// state.
							if (mCurFilter == null && newFilter == null) {
								return true;
							}
							if (mCurFilter != null
									&& mCurFilter.equals(newFilter)) {
								return true;
							}
							mCurFilter = newFilter;
							// getLoaderManager().restartLoader(0, null,
							// FriendsList.this);
							return true;
						}
					});
			SearchViewCompat.setOnCloseListener(searchView,
					new OnCloseListenerCompat() {
						@Override
						public boolean onClose() {
							if (!TextUtils.isEmpty(SearchViewCompat
									.getQuery(searchView))) {
								SearchViewCompat.setQuery(searchView, null,
										true);
							}
							return true;
						}
					});
			MenuItemCompat.setActionView(item, searchView);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Insert desired behavior here.
		Intent intent = new Intent(this.getActivity(),
				BluetoothChatActivity.class);
		startActivity(intent);
	}

	public Loader<List<UserEntity>> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created. This
		// sample only has one Loader, so we don't care about the ID.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.
		return new FriendListLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<UserEntity>> loader,
			List<UserEntity> data) {
		// The list should now be shown.
		// Set the new data in the adapter.
		adapter.setData(data);

		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<UserEntity>> arg0) {
		// TODO Auto-generated method stub
		adapter.setData(null);
	}
}

class FriendListLoader extends AsyncTaskLoader<List<UserEntity>> {
	List<UserEntity> friends;

	public FriendListLoader(Context context) {
		super(context);
	}

	@Override
	public List<UserEntity> loadInBackground() {
		final Context context = getContext();
		// TODO
		//List<UserEntity> friends = null;
		JSONArray friends = null;
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("action", "getOnLineUsers");
			String message = HttpClientHelper.blockSendMessage(UserFragment.userName, jsonObject.toString(), context);
			friends = new JSONArray(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (friends == null) {
			//friends = new ArrayList<UserEntity>();
			friends = new JSONArray();
		}

		// Create corresponding array of entries and load their labels.
		List<UserEntity> entries = new ArrayList<UserEntity>(friends.length());
		try {
			for (int i = 0; i < friends.length(); i++) {
				UserEntity entry;
					entry = new UserEntity(friends.getJSONObject(i).getString("alias"));
				entries.add(entry);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Done!
		return entries;
	}

	/**
	 * Called when there is new data to deliver to the client. The super class
	 * will take care of delivering it; the implementation here just adds a
	 * little more logic.
	 */
	@Override
	public void deliverResult(List<UserEntity> oldFriends) {
		if (isReset()) {
			// An async query came in while the loader is stopped. We
			// don't need the result.
			if (oldFriends != null) {
				onReleaseResources(oldFriends);
			}
		}
		// List<BusEntry> oldApps = oldBuses;
		// buses = oldBuses;

		if (isStarted()) {
			// If the Loader is currently started, we can immediately
			// deliver its results.
			super.deliverResult(oldFriends);
		}

		// At this point we can release the resources associated with
		// 'oldApps' if needed; now that the new result is delivered we
		// know that it is no longer in use.
		if (oldFriends != null) {
			onReleaseResources(oldFriends);
		}
	}

	/**
	 * Handles a request to start the Loader.
	 */
	@Override
	protected void onStartLoading() {
		if (friends != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(friends);
		}

		if (takeContentChanged() || friends == null) {// || configChange) {
			// If the data has changed since the last time it was loaded
			// or is not currently available, start a load.
			forceLoad();
		}
	}

	/**
	 * Handles a request to stop the Loader.
	 */
	@Override
	protected void onStopLoading() {
		// Attempt to cancel the current load task if possible.
		cancelLoad();
	}

	/**
	 * Handles a request to cancel a load.
	 */
	@Override
	public void onCanceled(List<UserEntity> friends) {
		super.onCanceled(friends);

		// At this point we can release the resources associated with 'apps'
		// if needed.
		onReleaseResources(friends);
	}

	/**
	 * Handles a request to completely reset the Loader.
	 */
	@Override
	protected void onReset() {
		super.onReset();

		// Ensure the loader is stopped
		onStopLoading();

		// At this point we can release the resources associated with 'friends'
		// if needed.
		if (friends != null) {
			onReleaseResources(friends);
			friends = null;
		}
	}

	/**
	 * Helper function to take care of releasing resources associated with an
	 * actively loaded data set.
	 */
	protected void onReleaseResources(List<UserEntity> friends) {
		// For a simple List<> there is nothing to do. For something
		// like a Cursor, we would close it here.
	}
}

class FriendListAdapter extends ArrayAdapter<UserEntity> {
	private final LayoutInflater mInflater;

	public FriendListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setData(List<UserEntity> data) {
		clear();
		if (data != null) {
			for (UserEntity appEntry : data) {
				add(appEntry);
			}
		}
	}

	/**
	 * Populate new items in the list.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.list_item_icon_text, parent,
					false);
		} else {
			view = convertView;
		}

		UserEntity item = getItem(position);
		((ImageView) view.findViewById(R.id.icon)).setImageDrawable(item
				.getIcon());
		((TextView) view.findViewById(R.id.text)).setText(item.getAlias());

		return view;
	}
}
