package com.blogspot.zone4apk.bookstash;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.zone4apk.bookstash.ViewPager.ViewPagerAdapter;
import com.blogspot.zone4apk.bookstash.recyclerViewDashboard.ProductItem;
import com.blogspot.zone4apk.bookstash.recyclerViewDashboard.ProductViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static java.lang.Thread.sleep;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ConnectivityReciever.ConnectivityRecieverListener {

    boolean doubleBackToExitPressedOnce = false;
    View hView;
    TextView nav_user_email;
    TextView nav_user_name;
    ImageView nav_user_img;
    //Using RecylerView to show the shopping items
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter adapter;
    //FirebaseDatabase
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    String quantity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Reciever content
        filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        reciever = new ConnectivityReciever();

        //Sliding Content
        viewPager = findViewById(R.id.viewPager);
        viewPager.getLayoutParams().height = (((WindowManager) getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getHeight() / 4);

        mSetupViewPager();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading Products...");
        // progressDialog.show();
        //To be re-enabled when in use

        //Setting recycler view-----------------------------------------------------------
        recyclerView = findViewById(R.id.recyclerview_dashboard);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("ProductDetailsDatabase");
        databaseReference.keepSynced(true);
        Query query = databaseReference.limitToLast(50);
        FirebaseRecyclerOptions<ProductItem> options = new FirebaseRecyclerOptions.Builder<ProductItem>().setQuery(query, ProductItem.class).build();

        adapter = new FirebaseRecyclerAdapter<ProductItem, ProductViewHolder>(options) {

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
                return new ProductViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final ProductViewHolder holder, int position, @NonNull final ProductItem model) {
                holder.setText_name(model.getName());
                holder.setText_description(model.getDescription());
                holder.setText_price("\u20B9 " + String.valueOf(model.getPrice()));
                holder.setImage(model.getImageurl(), getApplicationContext());
                holder.setText_quantity(model.getQuantity());
                holder.setItemId(model.getItemId());


                //----------Recycler Click Event------------
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       /* Snackbar
                                .make(view, "Sorry! We provide only subscription facility in your area.", Snackbar.LENGTH_LONG)
                                .show();*/
                        CharSequence options[] = new CharSequence[]{"Add to Cart", "Add to Wishlist"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                        builder.setTitle("Select Option");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //click event handler
                                if (which == 0) {
                                    //Added to cart
                                    DatabaseReference cartDatabase = FirebaseDatabase.getInstance().getReference().child("CartDatabase").child(mAuth.getCurrentUser().getUid()).child(model.getItemId());
                                    cartDatabase.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            quantity = (String) dataSnapshot.child("quantity").getValue();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });

                                    Map addToCartProductDetails = new HashMap();
                                    addToCartProductDetails.put("name", model.getName());
                                    addToCartProductDetails.put("description", model.getDescription());
                                    addToCartProductDetails.put("quantity", model.getQuantity());
                                    addToCartProductDetails.put("price", model.getPrice());
                                    addToCartProductDetails.put("image_url", model.getImageurl());
                                    addToCartProductDetails.put("itemId", model.getItemId());
                                    addToCartProductDetails.put("product_count", 1);

                                    cartDatabase.updateChildren(addToCartProductDetails, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                //If database update was successful.
                                                Toast.makeText(DashboardActivity.this, "Item is Added to cart", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }

                                if (which == 1) {
                                    //Added to wish list
                                    DatabaseReference wishListDatabase = FirebaseDatabase.getInstance().getReference().child("WishlistDatabase").child(mAuth.getCurrentUser().getUid().toString()).child(model.getItemId());
                                    Map addToWishProductDetails = new HashMap();
                                    addToWishProductDetails.put("name", model.getName());
                                    addToWishProductDetails.put("description", model.getDescription());
                                    addToWishProductDetails.put("quantity", model.getQuantity());
                                    addToWishProductDetails.put("price", model.getPrice());
                                    addToWishProductDetails.put("image_url", model.getImageurl());
                                    addToWishProductDetails.put("itemId", model.getItemId());
                                    wishListDatabase.updateChildren(addToWishProductDetails, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                //If database update was successful
                                                Toast.makeText(DashboardActivity.this, "Item is Added to WishList", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                        //need to be changed when in use
                    }
                });
            }
        };
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        //showing number of items in the view
        //counting total no of items and updating no content page accordingly
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long i = dataSnapshot.getChildrenCount();
                //showing view when there is any item in cart.
                if (i == 0) {//hiding views when there is no item in cart.

                } else {
                    //to be re-enabled when used
                    // Toast.makeText(DashboardActivity.this, String.format("Showing %d products", i), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        //AppDrawer-----------------------------------------------------
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

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        //to be re-enabled when in use
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectivityReciever.snackbar = null;
        //to be re-enabled when in use
        adapter.stopListening();
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            nav_user_name.setVisibility(View.VISIBLE);
            nav_user_name.setText(user.getDisplayName());
            nav_user_email.setVisibility(View.VISIBLE);
            nav_user_email.setText(user.getEmail());
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
                finishAffinity();
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
            Intent intentCart = new Intent(DashboardActivity.this, CartActivity.class);
            startActivity(intentCart);
            return true;
        }
        if (id == R.id.action_notification) {
            Intent intentCart = new Intent(DashboardActivity.this, NotificationsActivity.class);
            startActivity(intentCart);
            return true;
        }
        if (id == R.id.action_mysubscription) {
            Intent intentCart = new Intent(DashboardActivity.this, ProductSubscriptionActivity.class);
            startActivity(intentCart);
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
        } else if (id == R.id.nav_subscriptions) {
            startActivity(new Intent(getApplicationContext(), ProductSubscriptionActivity.class));
        } else if (id == R.id.nav_notification) {
            startActivity(new Intent(getApplicationContext(), NotificationsActivity.class));
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


    //share app with appshare popup
    public void mShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hey! Check out Gwala Dairy: A dairy at your doorstep! An app to buy dairy products online.\nhttps://play.google.com/store/apps/details?id=com.blogspot.zone4apk.bookstash";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.promomessage));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    //share app on whatsapp
    private void mWhatsShare() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage("com.whatsapp");
        String shareBody = "Hey! Check out Gwala Dairy: A dairy at your doorstep! \nAn app to buy dairy products online.\nhttps://play.google.com/store/apps/details?id=com.blogspot.zone4apk.bookstash";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.promomessage));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        try {
            startActivity(sharingIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry! Can't find Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }


    //Sliding Content
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    long slideCount;
    Timer timer;

    private void mSetupViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getApplicationContext());
        FirebaseDatabase.getInstance().getReference()
                .child("DashboardSliderDatabase")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        slideCount = dataSnapshot.getChildrenCount();
                        viewPagerAdapter.notifyDataSetChanged();
                        if (slideCount <= 1)
                            viewPager.setVisibility(View.GONE);
                        else
                            viewPager.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //Error occured
                    }
                });
        viewPager.setAdapter(viewPagerAdapter);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int index = viewPager.getCurrentItem();
                        if (slideCount > 1) {
                            //First slide is default and must be used as prototype
                            if (index == slideCount - 1) viewPager.setCurrentItem(1);
                            else viewPager.setCurrentItem(index + 1);
                        } else {
                            //When their is only default item in the view pager we have to hide it
                            viewPager.setCurrentItem(0);
                        }
                    }
                });
            }
        }, 1500, 2000);
    }


    //------------------------------Managing internet connection status
    //BroadcastReciever
    private ConnectivityReciever reciever;
    IntentFilter filter;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        ConnectivityReciever connectivityReciever = new ConnectivityReciever();
        connectivityReciever.showSnackbar(isConnected, findViewById(R.id.dashboard_activity));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(reciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //register connection status listener
        MyApplication.getInstance().setConnectivityListener(this);
        registerReceiver(reciever, filter);
    }

    public void mViewAllSubscription(View view) {
        startActivity(new Intent(getApplicationContext(), ProductSubscriptionActivity.class));
    }
}
