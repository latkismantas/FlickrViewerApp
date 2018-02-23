package uk.ac.kent.ml555.flickrviewer;


        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.drawable.Drawable;
        import android.os.Bundle;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v4.content.res.ResourcesCompat;
        import android.support.v4.widget.SwipeRefreshLayout;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.transition.Explode;
        import android.transition.Fade;
        import android.view.View;
        import android.support.design.widget.NavigationView;
        import android.support.v4.view.GravityCompat;
        import android.support.v4.widget.DrawerLayout;
        import android.support.v7.app.ActionBarDrawerToggle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.GridView;
        import android.widget.ImageView;
        import android.widget.ProgressBar;
        import android.widget.SearchView;
        import android.widget.Toast;

        import com.android.volley.Request;
        import com.android.volley.RequestQueue;
        import com.android.volley.Response;
        import com.android.volley.VolleyError;
        import com.android.volley.toolbox.JsonObjectRequest;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    ImageListAdapter adapter;
    ProgressBar prgBar;
    SwipeRefreshLayout swipeRefreshLayout ;
    SearchView searchView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.photoListView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerView.hasFixedSize();
        recyclerView.setItemViewCacheSize(10);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.imageList = NetworkMgr.getInstance(this).imageList;

        prgBar = (ProgressBar) findViewById(R.id.progressBar);
        prgBar.setVisibility(View.VISIBLE);
        NetworkMgr netMgr = NetworkMgr.getInstance(getApplicationContext());
        RequestQueue requestQueue = netMgr.requestQueue;

        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=7bb64ace0a36af298d53bf2f0a26332a&format=json&nojsoncallback=?&extras=description,owner_name,url_m,url_l,url_o,date_taken&per_page=50";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        requestQueue.add(request);

       final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();

                swipeRefreshLayout.setRefreshing(false);

            }
        });


    }

    void refresh(){

        adapter.clearList();
        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.imageList = NetworkMgr.getInstance(this).imageList;

        prgBar = (ProgressBar) findViewById(R.id.progressBar);
        prgBar.setVisibility(View.VISIBLE);

        NetworkMgr netMgr = NetworkMgr.getInstance(getApplicationContext());
        RequestQueue requestQueue = netMgr.requestQueue;

        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.getRecent&api_key=7bb64ace0a36af298d53bf2f0a26332a&format=json&nojsoncallback=?&extras=description,owner_name,url_m,url_l,url_o,date_taken&per_page=50";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        requestQueue.add(request);

    }

    void search(String query){

        adapter.clearList();
        adapter = new ImageListAdapter(this);
        recyclerView.setAdapter(adapter);

        adapter.imageList = NetworkMgr.getInstance(this).imageList;

        prgBar = (ProgressBar) findViewById(R.id.progressBar);
        prgBar.setVisibility(View.VISIBLE);

        NetworkMgr netMgr = NetworkMgr.getInstance(getApplicationContext());
        RequestQueue requestQueue = netMgr.requestQueue;

        String url = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=7bb64ace0a36af298d53bf2f0a26332a&text="+query+"&format=json&nojsoncallback=?&extras=description,owner_name,url_m,url_l,url_o,date_taken&per_page=50";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, responseListener, errorListener);

        requestQueue.add(request);

    }

    private Response.Listener<JSONObject> responseListener = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            try{

                JSONObject photos = response.getJSONObject("photos");
                JSONArray photoList = photos.getJSONArray("photo");

                for(int i=0; i < photoList.length(); i++){

                    ImageInfo newImage = new ImageInfo();

                    JSONObject photo = photoList.getJSONObject(i);

                    newImage.id = photo.getString("id");
                    newImage.title = photo.getString("title");


                    if(photo.has("owner")) {
                        newImage.owner = photo.getString("owner");
                    }else{
                        newImage.owner = "unknown";
                    }


                    if(photo.has("url_m")) {
                        newImage.url_m = photo.getString("url_m");
                    }
                    if(photo.has("url_l")){
                        newImage.url_l = photo.getString("url_l");
                    }
                    if(photo.has("url_o")){
                        newImage.url_o = photo.getString("url_o");
                    }

                    JSONObject descrObj = photo.getJSONObject("description");
                    newImage.description = descrObj.getString("_content");

                    NetworkMgr.getInstance(MainActivity.this).imageList.add(newImage);

                }
            } catch (JSONException ex) {
                ex.printStackTrace();

            }
            adapter.notifyDataSetChanged();
            prgBar.setVisibility(View.GONE);

        }


    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {

        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                search(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent (MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favorites) {
            Intent intent = new Intent (MainActivity.this, FavoritesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent (MainActivity.this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent (MainActivity.this, SettingsActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_delete) {
            AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(MainActivity.this);
            dlgBuilder.setTitle("Delete");
            dlgBuilder.setMessage("Do you want to DELETE all your Favorites?");
            dlgBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    MainActivity.this.deleteDatabase("favorites");
                    Toast.makeText(MainActivity.this, "Favorites Deleted", Toast.LENGTH_SHORT).show();

                }
            });

            dlgBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int which){
                    //do nothig
                }
            });
            AlertDialog dialog = dlgBuilder.create();
            dialog.show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}

