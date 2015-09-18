package com.bill.metrobus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SearchViewCompat;
import android.support.v4.widget.SearchViewCompat.OnCloseListenerCompat;
import android.support.v4.widget.SearchViewCompat.OnQueryTextListenerCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bill.metrobus.QuickAction.ActionItem;
import com.bill.metrobus.QuickAction.QuickAction;
import com.tencent.tencentmap.mapsdk.search.BusItem;
import com.tencent.tencentmap.mapsdk.search.BusSearch;

public class BusLineSearch extends FragmentActivity {

	private static String busName = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager fm = getSupportFragmentManager();

		// Create the list fragment and add it as our sole content.
		if (fm.findFragmentById(android.R.id.content) == null) {
			BusListFragment list = new BusListFragment();
			fm.beginTransaction().add(android.R.id.content, list).commit();
		}
	}


	/**
	 * Perform alphabetical comparison of application entry objects.
	 */
	public static final Comparator<BusEntry> ALPHA_COMPARATOR = new Comparator<BusEntry>() {
		private final Collator sCollator = Collator.getInstance();

		@Override
		public int compare(BusEntry object1, BusEntry object2) {
			return sCollator.compare(object1.getLabel().toString(), object2.getLabel().toString());
		}
	};

	/**
	 * A custom Loader that loads all of the installed applications.
	 */
	public static class BusListLoader extends AsyncTaskLoader<List<BusEntry>> {
		List<BusEntry> buses;

		public BusListLoader(Context context) {
			super(context);
		}

		@Override
		public List<BusEntry> loadInBackground() {
			final Context context = getContext();
			// TODO
			List<BusItem> buses = null;
			try {
				BusSearch busSearch = new BusSearch(context);
				if(busName==null){
					buses = busSearch.searchBus(MapFragment.CITY, "1").busItemList;
					buses.addAll(busSearch.searchBus(MapFragment.CITY, "2").busItemList);
				}else{
					buses = busSearch.searchBus(MapFragment.CITY, busName).busItemList;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (buses == null) {
				buses = new ArrayList<BusItem>();
			}

			// Create corresponding array of entries and load their labels.
			List<BusEntry> entries = new ArrayList<BusEntry>(buses.size());
			for (int i = 0; i < buses.size(); i++) {
				BusEntry entry = new BusEntry(buses.get(i), getContext());
				entry.loadLabel();
				entries.add(entry);
			}

			// Sort the list.
			Collections.sort(entries, ALPHA_COMPARATOR);

			// Done!
			return entries;
		}

		/**
		 * Called when there is new data to deliver to the client. The super
		 * class will take care of delivering it; the implementation here just
		 * adds a little more logic.
		 */
		@Override
		public void deliverResult(List<BusEntry> oldBuses) {
			if (isReset()) {
				// An async query came in while the loader is stopped. We
				// don't need the result.
				if (oldBuses != null) {
					onReleaseResources(oldBuses);
				}
			}
			//List<BusEntry> oldApps = oldBuses;
			//buses = oldBuses;

			if (isStarted()) {
				// If the Loader is currently started, we can immediately
				// deliver its results.
				super.deliverResult(oldBuses);
			}

			// At this point we can release the resources associated with
			// 'oldApps' if needed; now that the new result is delivered we
			// know that it is no longer in use.
			if (oldBuses != null) {
				onReleaseResources(oldBuses);
			}
		}

		/**
		 * Handles a request to start the Loader.
		 */
		@Override
		protected void onStartLoading() {
			if (buses != null) {
				// If we currently have a result available, deliver it
				// immediately.
				deliverResult(buses);
			}

			if (takeContentChanged() || buses == null){// || configChange) {
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
		public void onCanceled(List<BusEntry> buses) {
			super.onCanceled(buses);

			// At this point we can release the resources associated with 'apps'
			// if needed.
			onReleaseResources(buses);
		}

		/**
		 * Handles a request to completely reset the Loader.
		 */
		@Override
		protected void onReset() {
			super.onReset();

			// Ensure the loader is stopped
			onStopLoading();

			// At this point we can release the resources associated with 'apps'
			// if needed.
			if (buses != null) {
				onReleaseResources(buses);
				buses = null;
			}
		}

		/**
		 * Helper function to take care of releasing resources associated with
		 * an actively loaded data set.
		 */
		protected void onReleaseResources(List<BusEntry> apps) {
			// For a simple List<> there is nothing to do. For something
			// like a Cursor, we would close it here.
		}
	}

	public static class BusListAdapter extends ArrayAdapter<BusEntry> {
		private final LayoutInflater mInflater;

		public BusListAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_2);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public void setData(List<BusEntry> data) {
			clear();
			if (data != null) {
				for (BusEntry appEntry : data) {
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

			BusEntry item = getItem(position);
			((ImageView) view.findViewById(R.id.icon)).setImageDrawable(item
					.getIcon());
			((TextView) view.findViewById(R.id.text)).setText(item.getLabel());

			return view;
		}
	}

	public static class BusListFragment extends ListFragment implements
			LoaderManager.LoaderCallbacks<List<BusEntry>> {

		/**
		 * Listview selected row
		 */
		private int mSelectedRow = 0;
		
		/**
		 * Right arrow icon on each listview row
		 */
		//private ImageView mMoreIv = null;
		
		private Context context;
		// This is the Adapter being used to display the list's data.
		private BusListAdapter adapter;

		// If non-null, this is the current filter the user has provided.
		//private String mCurFilter;

		//private OnQueryTextListenerCompat mOnQueryTextListenerCompat;

		private Intent detailBusLineIntent;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			this.context = getActivity();
			// Give some text to display if there is no data. In a real
			// application this would come from a resource.
			setEmptyText("No buses");

			// We have a menu item to show in action bar.
			setHasOptionsMenu(true);
			
			detailBusLineIntent = new Intent(context, DetailBusLineActivity.class);
			
			// Create an empty adapter we will use to display the loaded data.
			adapter = new BusListAdapter(context);
			setListAdapter(adapter);

			// Start out with a progress indicator.
			setListShown(false);

			// Prepare the loader. Either re-connect with an existing one,
			// or start a new one.
			getLoaderManager().initLoader(0, null, this);
			
			ActionItem addItem = new ActionItem(0, "收藏", getResources().getDrawable(R.drawable.like_grey));
			//ActionItem acceptItem 	= new ActionItem(ID_ACCEPT, "Accept", getResources().getDrawable(R.drawable.coursetable_ic_accept));
	        //ActionItem uploadItem 	= new ActionItem(ID_UPLOAD, "Upload", getResources().getDrawable(R.drawable.coursetable_ic_up));
			
			final QuickAction mQuickAction = new QuickAction(context);
			
			mQuickAction.addActionItem(addItem);
			//mQuickAction.addActionItem(acceptItem);
			//mQuickAction.addActionItem(uploadItem);
			
			//setup the action item click listener
			mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
				public void onItemClick(QuickAction quickAction, int pos, int actionId) {
					ActionItem actionItem = quickAction.getActionItem(pos);
					
					if (actionId == 0) { //Add item selected
						Toast.makeText(context, "Love item selected on row " + mSelectedRow, Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(context, actionItem.getTitle() + " item selected on row " 
								+ mSelectedRow, Toast.LENGTH_SHORT).show();
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
					if(id<-1){
						return ;
					}
					BusEntry item = (BusEntry)parent.getAdapter().getItem((int)id);
					Bundle bundle = new Bundle();
					bundle.putString("bus_id", item.getBusInfo().uid);
					bundle.putString("bus_name", item.getBusInfo().name);
					bundle.putString("bus_start", item.getBusInfo().from);
					bundle.putString("bus_end", item.getBusInfo().to);
					detailBusLineIntent.putExtras(bundle);
					startActivity(detailBusLineIntent);
				}
			});
			getListView().setOnItemLongClickListener(new OnItemLongClickListener() {
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					mSelectedRow = position; //set the selected row
					
					mQuickAction.show(view);
					
					//change the right arrow icon to selected state 
					//mMoreIv = (ImageView) view.findViewById(R.id.i_more);
					//mMoreIv.setImageResource(R.drawable.coursetable_ic_list_more_selected);
					return true;
				}
			});
		}

		@Override
		public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
			// Place an action bar item for searching.
//			MenuItem itemLove = menu.add("love");
//			itemLove.setIcon(R.drawable.red_solid_like);
//			MenuItemCompat.setShowAsAction(itemLove,
//					MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
			
			MenuItem itemSearch = menu.add("搜索");
			itemSearch.setIcon(android.R.drawable.ic_menu_search);
			MenuItemCompat.setShowAsAction(itemSearch,
							MenuItemCompat.SHOW_AS_ACTION_IF_ROOM
									| MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
			final View searchView = SearchViewCompat
					.newSearchView(getActivity());
			if (searchView != null) {
				SearchViewCompat.setOnQueryTextListener(searchView,
						new OnQueryTextListenerCompat() {
							@Override
							public boolean onQueryTextChange(String newText) {
								// Called when the action bar search text has
								// changed. Since this
								// is a simple array adapter, we can just have
								// it do the filtering.
								//mCurFilter = !TextUtils.isEmpty(newText) ? newText
								//		: null;
								//adapter.getFilter().filter(mCurFilter);
								return true;
							}

							@Override
							public boolean onQueryTextSubmit(String query) {
								// TODO Auto-generated method stub
								busName = query;
								BusListFragment.this.getLoaderManager().getLoader(0).onContentChanged();
								return super.onQueryTextSubmit(query);
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
				SearchViewCompat.setSubmitButtonEnabled(searchView, true);
				
				//设置搜索框字体颜色
				AutoCompleteTextView search_text = (AutoCompleteTextView) searchView.findViewById(
						searchView.getContext().getResources().
						getIdentifier("android:id/search_src_text", null, null));
				search_text.setTextColor(Color.WHITE);
				
				MenuItemCompat.setActionView(itemSearch, searchView);
			}
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			// Insert desired behavior here.
			Log.i("LoaderCustom", "Item clicked: " + id);
		}

		@Override
		public Loader<List<BusEntry>> onCreateLoader(int id, Bundle args) {
			// This is called when a new Loader needs to be created. This
			// sample only has one Loader with no arguments, so it is simple.
			return new BusListLoader(getActivity());
		}

		@Override
		public void onLoadFinished(Loader<List<BusEntry>> loader,
				List<BusEntry> data) {
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
		public void onLoaderReset(Loader<List<BusEntry>> loader) {
			// Clear the data in the adapter.
			adapter.setData(null);
		}
	}

}
