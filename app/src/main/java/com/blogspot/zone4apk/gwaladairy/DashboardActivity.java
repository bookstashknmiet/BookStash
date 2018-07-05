package com.blogspot.zone4apk.gwaladairy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.zone4apk.gwaladairy.recyclerViewDashboard.ProductItem;
import com.blogspot.zone4apk.gwaladairy.recyclerViewDashboard.RecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static java.lang.Thread.sleep;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    boolean doubleBackToExitPressedOnce = false;
    View hView;
    TextView nav_user_email;
    TextView nav_user_name;
    ImageView nav_user_img;

    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<ProductItem> productItems;

    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setting recycler view
        recyclerView = findViewById(R.id.recyclerview_dashboard);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        if (recyclerView != null)
            recyclerView.setHasFixedSize(true);
        productItems = new ArrayList<>();

        //Products Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        getProductData();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        hView = navigationView.getHeaderView(0);
        nav_user_email = (TextView) hView.findViewById(R.id.textView_user_email);
        nav_user_name = (TextView) hView.findViewById(R.id.textView_user_name);
        nav_user_img = (ImageView) hView.findViewById(R.id.imageView_user);
        navigationView.setNavigationItemSelectedListener(this);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    void getProductData() {
        databaseReference = firebaseDatabase.getReference("ProductDetailsDatabase");
        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ProductItem productItem = new ProductItem();
                    productItem = snapshot.getValue(ProductItem.class);

                    //adding new item to arraylist
                    productItems.add(productItem);
                }
                //setting adapter
                recyclerView.setAdapter(new RecyclerAdapter(productItems, getApplicationContext()));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            nav_user_name.setVisibility(View.VISIBLE);
            nav_user_name.setText(user.getDisplayName());
            nav_user_email.setVisibility(View.VISIBLE);
            nav_user_email.setText(user.getEmail());
           /* Picasso.with(this)
                    .load(user.getPhotoUrl().toString()).transform(new CropCircleTransformation())
                    .into(nav_user_img);
                    */
            //since we do not have user image right now.
            Picasso.with(this)
                    .load(R.mipmap.ic_launcher)
                    .transform(new CropCircleTransformation())
                    .into(nav_user_img);

        } else {
            Picasso.with(this)
                    .load(R.mipmap.ic_launcher)
                    .transform(new CropCircleTransformation())
                    .into(nav_user_img);
            nav_user_name.setText(R.string.nav_header_title);
            nav_user_email.setText("Please sign in.");
            nav_user_email.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                System.exit(0);
            } else {
                this.doubleBackToExitPressedOnce = true;
                Snackbar.make(findViewById(R.id.drawer_layout), "Please click back again to exit", Snackbar.LENGTH_SHORT).show();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            doubleBackToExitPressedOnce = false;
                        }
                    }
                });
                t.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_mycart) {
            Toast.makeText(this, "Cart is pressed", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_notification) {
            Toast.makeText(this, "Notification is pressed", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
        } else if (id == R.id.nav_help) {
            startActivity(new Intent(getApplicationContext(), HelpActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
        } else if (id == R.id.nav_my_orders) {
            startActivity(new Intent(getApplicationContext(), MyOrdersActivity.class));
        } else if (id == R.id.nav_wishlist) {
            startActivity(new Intent(getApplicationContext(), WishlistActivity.class));
        } else if (id == R.id.nav_share) {
            mShare();
        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "Sharing via Whatsapp!", Toast.LENGTH_SHORT).show();
            mWhatsShare();
        }

        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //share app
    public void mShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey! Check out Gwala Dairy: A dairy at your doorstep! An app to buy dairy products online.\nhttps://play.google.com/store/apps/details?id=com.blogspot.zone4apk.gwaladairy";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.promomessage));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    //share app on whatsapp
    private void mWhatsShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage("com.whatsapp");
        String shareBody = "Hey! Check out Gwala Dairy: A dairy at your doorstep! \nAn app to buy dairy products online.\nhttps://play.google.com/store/apps/details?id=com.blogspot.zone4apk.gwaladairy";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.promomessage));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        try {
            startActivity(sharingIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry! Can't find Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

}
