package com.example.swiftmart.Frgments;

import static com.example.swiftmart.R.drawable.category_card;
import static com.example.swiftmart.R.drawable.category_card1;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.swiftmart.Adapter.ExploreProductAdapter;
import com.example.swiftmart.Adapter.ProductAdapter;
import com.example.swiftmart.Model.ProductModel;
import com.example.swiftmart.R;
import com.example.swiftmart.Utils.CustomToast;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {
    //Category
    private LinearLayout mobiles, earbuds, tv, laptop, headphone, speaker, keyword, mouse, camera, smartwatch, tablet; // All category
    private LinearLayout mobiles1, earbuds1, tv1, laptop1, headphone1, speaker1, keyword1, mouse1, camera1, smartwatch1, tablet1; // All category

    //Mobile company
    private LinearLayout iphone, samsung, vivo, oppo, mi, realme, motorola, poco, goggle, oneplus, iqoo, nothing;
    //Earbuds company
    private LinearLayout boatlogo, realmelogo, onepluslogo, nothinglogo, triggerlogo, trukelogo;
    //TV company
    private LinearLayout samsunglogo, lglogo, milogo, tcllogo;
    //Laptops company
    private LinearLayout hplogo, dellogo, lenovologo, acerlogo, asusogo, applelogo, msilogo;
    //Headphone company
    private LinearLayout headphoneBoat, headphoneJBL, headphoneCosmicByte, headphoneSennheiner, headphoneZebronics, headphoneSony;
    //speaker company
    private LinearLayout speakerBoat, speakerJBL, speakerLG, speakerBoult;
    // mouse comapny
    private LinearLayout mouseHp, mouseDell, mouseLogitech, mouseLenovo, mouseZebronics;
    //keyboard company
    private LinearLayout keyboardHp, keyboardDell, keyboardLogitech, keyboardLenovo, keyboardZebronics;
    //camera company
    private LinearLayout cameraNikon, cameraCanon, cameraSony, cameraFujifilm, cameraPanasonic;
    //smartwatch company
    private LinearLayout smartwatchApple, smartwatchSamsung, smartwatchNoise, smartwatchFirebolt;
    //tablet company
    private LinearLayout tabletApple, tabletSamsung, tabletRealme, tabletRedmi, tabletOneplus, tabletPoco;

    //company horizontal scrollview
    private HorizontalScrollView mobileScrollView, earphoneHorizontalScrollView, tvHorizontalScrollView, laptopHorizontalScrollView,headphoneHorizontalScrollView,speakerHorizontalScrollView,keyboardHorizontalScrollView,mouseHorizontalScrollView,cameraHorizontalScrollView,smartwatchHorizontalScrollView,tabletHorizontalScrollView;
    // category indicator
    private View mobileIndicator, earbudsIndicator, tvIndicator, laptopsIndicator, headphonesIndicator, speakersIndicator, keyboardIndicator, mouseIndicator, cameraIndicator, smartwatchesIndicator, tabletsIndicator;

    private ScrollView homeFragmentScrollView;

    private ArrayList<ProductModel> datalist = new ArrayList<>();
    private RecyclerView adminCategoryRecyclerview;
    private FirebaseFirestore db;
    private ExploreProductAdapter adapter;

    private ListenerRegistration categoryListener;
    private ListenerRegistration filteredDataListener;

    public CategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        db = FirebaseFirestore.getInstance();

        initViews(view);
        getAllProducts();
        setCategoryClickListeners();
        setMobileCompanyClickListeners();
        setEarbudsCompanyClickListeners();
        setTVCompanyClickListeners();
        setLaptopCompanyClickListeners();
        setHeadphoneCompanyClickListeners();
        setSpeakerCompanyClickListeners();
        setKeyboardCompanyClickListeners();
        setMouseCompanyClickListeners();
        setCameraCompanyClickListeners();
        setSmartwatchCompanyClickListeners();
        setTabletCompanyClickListeners();

        handleOnBackPress();

        return view;
    }

    // Initialize all views
    private void initViews(View view) {
        // recycler view
        adminCategoryRecyclerview = view.findViewById(R.id.categoryRecyclerview);

        // all categories
        mobiles = view.findViewById(R.id.mobiles);
        earbuds = view.findViewById(R.id.earbuds);
        tv = view.findViewById(R.id.tv);
        laptop = view.findViewById(R.id.laptop);
        headphone = view.findViewById(R.id.headphone);
        speaker = view.findViewById(R.id.speaker);
        keyword = view.findViewById(R.id.keyword);
        mouse = view.findViewById(R.id.mouse);
        camera = view.findViewById(R.id.camera);
        smartwatch = view.findViewById(R.id.smartwatch);
        tablet = view.findViewById(R.id.tablet);


        mobiles1 = view.findViewById(R.id.mobiles1);
        earbuds1 = view.findViewById(R.id.earbuds1);
        tv1 = view.findViewById(R.id.tv1);
        laptop1 = view.findViewById(R.id.laptop1);
        headphone1 = view.findViewById(R.id.headphone1);
        speaker1 = view.findViewById(R.id.speaker1);
        keyword1 = view.findViewById(R.id.keyword1);
        mouse1 = view.findViewById(R.id.mouse1);
        camera1 = view.findViewById(R.id.camera1);
        smartwatch1 = view.findViewById(R.id.smartwatch1);
        tablet1 = view.findViewById(R.id.tablet1);

        // category indicators
        mobileIndicator = view.findViewById(R.id.mobileIndicator);
        earbudsIndicator = view.findViewById(R.id.earbudsIndicator);
        tvIndicator = view.findViewById(R.id.tvIndicator);
        laptopsIndicator = view.findViewById(R.id.laptopsIndicator);
        headphonesIndicator = view.findViewById(R.id.headphonesIndicator);
        speakersIndicator = view.findViewById(R.id.speakersIndicator);
        keyboardIndicator = view.findViewById(R.id.keyboardIndicator);
        mouseIndicator = view.findViewById(R.id.mouseIndicator);
        cameraIndicator = view.findViewById(R.id.cameraIndicator);
        smartwatchesIndicator = view.findViewById(R.id.smartwatchesIndicator);
        tabletsIndicator = view.findViewById(R.id.tabletsIndicator);

        // mobile companies
        iphone = view.findViewById(R.id.iphone);
        samsung = view.findViewById(R.id.samsung);
        vivo = view.findViewById(R.id.vivo);
        oppo = view.findViewById(R.id.oppo);
        mi = view.findViewById(R.id.mi);
        realme = view.findViewById(R.id.realme);
        motorola = view.findViewById(R.id.motorola);
        poco = view.findViewById(R.id.poco);
        goggle = view.findViewById(R.id.goggle);
        oneplus = view.findViewById(R.id.oneplus);
        iqoo = view.findViewById(R.id.iqoo);
        nothing = view.findViewById(R.id.nothing);

        // earbuds companies
        boatlogo = view.findViewById(R.id.boatlogo);
        realmelogo = view.findViewById(R.id.realmelogo);
        onepluslogo = view.findViewById(R.id.onepluslogo);
        nothinglogo = view.findViewById(R.id.nothinglogo);
        triggerlogo = view.findViewById(R.id.triggerlogo);
        trukelogo = view.findViewById(R.id.trukelogo);

        // tv company
        samsunglogo = view.findViewById(R.id.samsunglogo);
        lglogo = view.findViewById(R.id.lglogo);
        milogo = view.findViewById(R.id.milogo);
        tcllogo = view.findViewById(R.id.tcllogo);

        // laptops company
        hplogo = view.findViewById(R.id.hplogo);
        dellogo = view.findViewById(R.id.dellogo);
        lenovologo = view.findViewById(R.id.lenovologo);
        acerlogo = view.findViewById(R.id.acerlogo);
        asusogo = view.findViewById(R.id.asusogo);
        applelogo = view.findViewById(R.id.applelogo);
        msilogo = view.findViewById(R.id.msilogo);

        //headphone company
        headphoneBoat = view.findViewById(R.id.headphoneBoat);
        headphoneJBL = view.findViewById(R.id.headphoneJBL);
        headphoneCosmicByte = view.findViewById(R.id.headphoneCosmicByte);
        headphoneSennheiner = view.findViewById(R.id.headphoneSennheiser);
        headphoneZebronics = view.findViewById(R.id.headphoneZebronics);
        headphoneSony = view.findViewById(R.id.headphoneSony);

        //speaker comapny
        speakerBoat = view.findViewById(R.id.speakerBoat);
        speakerJBL = view.findViewById(R.id.speakerJBL);
        speakerLG = view.findViewById(R.id.speakerLG);
        speakerBoult = view.findViewById(R.id.speakerBoult);

        //keyboard company
        keyboardHp = view.findViewById(R.id.keyboardHP);
        keyboardDell = view.findViewById(R.id.keyboardDell);
        keyboardLogitech = view.findViewById(R.id.keyboardLogitech);
        keyboardLenovo = view.findViewById(R.id.keyboardLenovo);
        keyboardZebronics = view.findViewById(R.id.keyboardZebronics);

        //mouse company
        mouseHp = view.findViewById(R.id.mouseHP);
        mouseDell = view.findViewById(R.id.mouseDell);
        mouseLogitech = view.findViewById(R.id.mouseLogitech);
        mouseLenovo = view.findViewById(R.id.mouseLenovo);
        mouseZebronics = view.findViewById(R.id.mouseZebronics);

        //camera company
        cameraNikon = view.findViewById(R.id.cameraNikon);
        cameraCanon = view.findViewById(R.id.cameraCanon);
        cameraSony = view.findViewById(R.id.cameraSony);
        cameraFujifilm = view.findViewById(R.id.cameraFujifilm);
        cameraPanasonic = view.findViewById(R.id.cameraPanasonic);

        //smart watch company
        smartwatchApple = view.findViewById(R.id.smartwatchApple);
        smartwatchSamsung = view.findViewById(R.id.smartwatchSamsung);
        smartwatchNoise = view.findViewById(R.id.smartwatchNoise);
        smartwatchFirebolt = view.findViewById(R.id.smartwatchFirebolt);

        //Tablet company
        tabletApple = view.findViewById(R.id.tabletApple);
        tabletSamsung = view.findViewById(R.id.tabletSamsung);
        tabletRealme = view.findViewById(R.id.tabletRealme);
        tabletRedmi = view.findViewById(R.id.tabletRedmi);
        tabletOneplus = view.findViewById(R.id.tabletOneplus);
        tabletPoco = view.findViewById(R.id.tabletPoco);

        homeFragmentScrollView = view.findViewById(R.id.homeFragmentScrollView);
        mobileScrollView = view.findViewById(R.id.mobileHorizontalScrollView);
        earphoneHorizontalScrollView = view.findViewById(R.id.earphoneHorizontalScrollView);
        tvHorizontalScrollView = view.findViewById(R.id.tvHorizontalScrollView);
        laptopHorizontalScrollView = view.findViewById(R.id.laptopHorizontalScrollView);
        headphoneHorizontalScrollView = view.findViewById(R.id.headphoneHorizontalScrollView);
        speakerHorizontalScrollView = view.findViewById(R.id.speakerHorizontalScrollView);
        keyboardHorizontalScrollView = view.findViewById(R.id.keyboardHorizontalScrollView);
        mouseHorizontalScrollView = view.findViewById(R.id.mouseHorizontalScrollView);
        cameraHorizontalScrollView = view.findViewById(R.id.cameraHorizontalScrollView);
        smartwatchHorizontalScrollView = view.findViewById(R.id.smartwatchHorizontalScrollView);
        tabletHorizontalScrollView = view.findViewById(R.id.tabletHorizontalScrollView);

        homeFragmentScrollView.setVerticalScrollBarEnabled(false);
        mobileScrollView.setHorizontalScrollBarEnabled(false);
        earphoneHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        tvHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        laptopHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        headphoneHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        speakerHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        keyboardHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        mouseHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        cameraHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        smartwatchHorizontalScrollView.setHorizontalScrollBarEnabled(false);
        tabletHorizontalScrollView.setHorizontalScrollBarEnabled(false);

    }

    // Get all the products from the database
    private void getAllProducts(){
        adminCategoryRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        datalist.clear();

        db.collection("Products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null){
                            CustomToast.showToast(getContext(),  "Error in data fetching");
                            return;
                        }

                        if (value != null && !value.isEmpty()){
                            for (QueryDocumentSnapshot documentSnapshot : value){
                                ProductModel productModel = documentSnapshot.toObject(ProductModel.class);
                                datalist.add(productModel);

                                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                                adminCategoryRecyclerview.setLayoutManager(layoutManager);
                                adapter = new ExploreProductAdapter(getContext(), datalist);
                                adminCategoryRecyclerview.setHasFixedSize(true);
                                adminCategoryRecyclerview.setAdapter(adapter);
                                adminCategoryRecyclerview.setItemAnimator(new DefaultItemAnimator());
                            }
                        }
                    }
                });
    }

    // Set click on category
    private void setCategoryClickListeners() {
        mobiles.setOnClickListener(v -> {
            handleCategoryClick("Mobile", mobileScrollView);
            selectIndicator(mobileIndicator);
            mobile();
        });
        earbuds.setOnClickListener(v -> {
            handleCategoryClick("AirBuds", earphoneHorizontalScrollView);
            selectIndicator(earbudsIndicator);
            earbuds();
        });
        tv.setOnClickListener(v -> {
            handleCategoryClick("TV", tvHorizontalScrollView);
            selectIndicator(tvIndicator);
            tv();
        });
        laptop.setOnClickListener(v -> {
            handleCategoryClick("Laptop", laptopHorizontalScrollView);
            selectIndicator(laptopsIndicator);
            leptop();
        });
        headphone.setOnClickListener(v -> {
            handleCategoryClick("Headphone", headphoneHorizontalScrollView);
            selectIndicator(headphonesIndicator);
            headphone();
        });
        speaker.setOnClickListener(v -> {
            handleCategoryClick("Speaker", speakerHorizontalScrollView);
            selectIndicator(speakersIndicator);
            speaker();
        });
        keyword.setOnClickListener(v -> {
            handleCategoryClick("Keyboard", keyboardHorizontalScrollView);
            selectIndicator(keyboardIndicator);
            keyword();
        });
        mouse.setOnClickListener(v -> {
            handleCategoryClick("Mouse", mouseHorizontalScrollView);
            selectIndicator(mouseIndicator);
            mouse();
        });
        camera.setOnClickListener(v -> {
            handleCategoryClick("Camera", cameraHorizontalScrollView);
            selectIndicator(cameraIndicator);
            camera();
        });
        smartwatch.setOnClickListener(v -> {
            handleCategoryClick("SmartWatch", smartwatchHorizontalScrollView);
            selectIndicator(smartwatchesIndicator);
            smartwatch();
        });
        tablet.setOnClickListener(v -> {
            handleCategoryClick("Tablet", tabletHorizontalScrollView);
            selectIndicator(tabletsIndicator);
            tablet();
        });
    }



    public  void mobile(){
       mobiles1.setBackgroundResource(category_card1);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void earbuds(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card1);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void tv(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card1);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void leptop(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card1);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void headphone(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card1);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void speaker(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card1);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void keyword(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card1);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void mouse(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card1);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void camera(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card1);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card);

    } public  void smartwatch(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card1);
        tablet1.setBackgroundResource(category_card);

    }public  void tablet(){
       mobiles1.setBackgroundResource(category_card);
        earbuds1.setBackgroundResource(category_card);
        tv1.setBackgroundResource(category_card);
        laptop1.setBackgroundResource(category_card);
        headphone1.setBackgroundResource(category_card);
        speaker1.setBackgroundResource(category_card);
        keyword1.setBackgroundResource(category_card);
        mouse1.setBackgroundResource(category_card);
        camera1.setBackgroundResource(category_card);
        smartwatch1.setBackgroundResource(category_card);
        tablet1.setBackgroundResource(category_card1);

    }
    // Set click on mobile company
    private void setMobileCompanyClickListeners() {
        iphone.setOnClickListener(v -> fetchFilteredData("Mobile", "Apple"));
        samsung.setOnClickListener(v -> fetchFilteredData("Mobile", "Samsung"));
        vivo.setOnClickListener(v -> fetchFilteredData("Mobile", "Vivo"));
        oppo.setOnClickListener(v -> fetchFilteredData("Mobile", "Oppo"));
        mi.setOnClickListener(v -> fetchFilteredData("Mobile", "Xiaomi"));
        realme.setOnClickListener(v -> fetchFilteredData("Mobile", "Realme"));
        motorola.setOnClickListener(v -> fetchFilteredData("Mobile", "Motorola"));
        poco.setOnClickListener(v -> fetchFilteredData("Mobile", "Poco"));
        goggle.setOnClickListener(v -> fetchFilteredData("Mobile", "Google"));
        oneplus.setOnClickListener(v -> fetchFilteredData("Mobile", "OnePlus"));
        iqoo.setOnClickListener(v -> fetchFilteredData("Mobile", "Iqoo"));
        nothing.setOnClickListener(v -> fetchFilteredData("Mobile", "Nothing"));
    }

    // Set click on earbuds company
    private void setEarbudsCompanyClickListeners() {
        boatlogo.setOnClickListener(v -> fetchFilteredData("AirBuds", "Boat"));
        realmelogo.setOnClickListener(v -> fetchFilteredData("AirBuds", "Realme"));
        onepluslogo.setOnClickListener(v -> fetchFilteredData("AirBuds", "OnePlus"));
        nothinglogo.setOnClickListener(v -> fetchFilteredData("AirBuds", "Nothing"));
        triggerlogo.setOnClickListener(v -> fetchFilteredData("AirBuds", "Trigger"));
        trukelogo.setOnClickListener(v -> fetchFilteredData("AirBuds", "Truke"));
    }

    // Set click on tv company
    private void setTVCompanyClickListeners() {
        samsunglogo.setOnClickListener(v -> fetchFilteredData("TV", "Samsung"));
        lglogo.setOnClickListener(v -> fetchFilteredData("TV", "LG"));
        milogo.setOnClickListener(v -> fetchFilteredData("TV", "Xiaomi"));
        tcllogo.setOnClickListener(v -> fetchFilteredData("TV", "TCL"));
    }

    // Set click on laptop company
    private void setLaptopCompanyClickListeners() {
        hplogo.setOnClickListener(v -> fetchFilteredData("Laptop", "HP"));
        dellogo.setOnClickListener(v -> fetchFilteredData("Laptop", "Dell"));
        lenovologo.setOnClickListener(v -> fetchFilteredData("Laptop", "Lenovo"));
        acerlogo.setOnClickListener(v -> fetchFilteredData("Laptop", "Acer"));
        asusogo.setOnClickListener(v -> fetchFilteredData("Laptop", "Asus"));
        applelogo.setOnClickListener(v -> fetchFilteredData("Laptop", "Apple"));
        msilogo.setOnClickListener(v -> fetchFilteredData("Laptop", "MSI"));
    }

    // Set click on headphone company
    private void setHeadphoneCompanyClickListeners() {
        headphoneBoat.setOnClickListener(v -> fetchFilteredData("Headphone", "Boat"));
        headphoneJBL.setOnClickListener(v -> fetchFilteredData("Headphone", "JBL"));
        headphoneCosmicByte.setOnClickListener(v -> fetchFilteredData("Headphone", "Cosmic Byte"));
        headphoneSennheiner.setOnClickListener(v -> fetchFilteredData("Headphone", "Sennheiser"));
        headphoneZebronics.setOnClickListener(v -> fetchFilteredData("Headphone", "Zebronics"));
        headphoneSony.setOnClickListener(v -> fetchFilteredData("Headphone", "Sony"));
    }

    // Set click on speaker company
    private void setSpeakerCompanyClickListeners() {
        speakerBoat.setOnClickListener(v -> fetchFilteredData("Speaker", "Boat"));
        speakerJBL.setOnClickListener(v -> fetchFilteredData("Speaker", "JBL"));
        speakerLG.setOnClickListener(v -> fetchFilteredData("Speaker", "LG"));
        speakerBoult.setOnClickListener(v -> fetchFilteredData("Speaker", "Boult"));
    }

    // Set click on mouse company
    private void setMouseCompanyClickListeners() {
        mouseHp.setOnClickListener(v -> fetchFilteredData("Mouse", "HP"));
        mouseDell.setOnClickListener(v -> fetchFilteredData("Mouse", "Dell"));
        mouseLogitech.setOnClickListener(v -> fetchFilteredData("Mouse", "Logitech"));
        mouseLenovo.setOnClickListener(v -> fetchFilteredData("Mouse", "Lenovo"));
        mouseZebronics.setOnClickListener(v -> fetchFilteredData("Mouse", "Zebronics"));
    }

    // Set click on keyboard company
    private void setKeyboardCompanyClickListeners() {
        keyboardHp.setOnClickListener(v -> fetchFilteredData("Keyboard", "HP"));
        keyboardDell.setOnClickListener(v -> fetchFilteredData("Keyboard", "Dell"));
        keyboardLogitech.setOnClickListener(v -> fetchFilteredData("Keyboard", "Logitech"));
        keyboardLenovo.setOnClickListener(v -> fetchFilteredData("Keyboard", "Lenovo"));
        keyboardZebronics.setOnClickListener(v -> fetchFilteredData("Keyboard", "Zebronics"));
    }

    // Set click on camera company
    private void setCameraCompanyClickListeners() {
        cameraNikon.setOnClickListener(v -> fetchFilteredData("Camera", "Nikon"));
        cameraCanon.setOnClickListener(v -> fetchFilteredData("Camera", "Canon"));
        cameraSony.setOnClickListener(v -> fetchFilteredData("Camera", "Sony"));
        cameraFujifilm.setOnClickListener(v -> fetchFilteredData("Camera", "Fujifilm"));
        cameraPanasonic.setOnClickListener(v -> fetchFilteredData("Camera", "Panasonic"));
    }

    // Set click on smartwatch company
    private void setSmartwatchCompanyClickListeners() {
        smartwatchApple.setOnClickListener(v -> fetchFilteredData("Smartwatch", "Apple"));
        smartwatchSamsung.setOnClickListener(v -> fetchFilteredData("Smartwatch", "Samsung"));
        smartwatchNoise.setOnClickListener(v -> fetchFilteredData("Smartwatch", "Noise"));
        smartwatchFirebolt.setOnClickListener(v -> fetchFilteredData("Smartwatch", "Firebolt"));
    }

    // Set click on tablet company
    private void setTabletCompanyClickListeners() {
        tabletApple.setOnClickListener(v -> fetchFilteredData("Tablet", "Apple"));
        tabletSamsung.setOnClickListener(v -> fetchFilteredData("Tablet", "Samsung"));
        tabletRealme.setOnClickListener(v -> fetchFilteredData("Tablet", "Realme"));
        tabletRedmi.setOnClickListener(v -> fetchFilteredData("Tablet", "Redmi"));
        tabletOneplus.setOnClickListener(v -> fetchFilteredData("Tablet", "OnePlus"));
        tabletPoco.setOnClickListener(v -> fetchFilteredData("Tablet", "Poco"));
    }


    private void handleCategoryClick(String category, HorizontalScrollView visibleScrollView) {
        clearData();
        showOnlySelectedScrollView(visibleScrollView);
        fetchCategoryData(category);
    }

    private void fetchCategoryData(String category) {
        if (categoryListener != null) {
            categoryListener.remove();
        }

        categoryListener = db.collection("Products")
                .whereEqualTo("category", category)
                .addSnapshotListener((value, error) -> handleSnapshotResult(value, error));
    }

    private void fetchFilteredData(String category, String company) {
        if (filteredDataListener != null) {
            filteredDataListener.remove();
        }

        filteredDataListener = db.collection("Products")
                .whereEqualTo("category", category)
                .whereEqualTo("company", company)
                .addSnapshotListener((value, error) -> handleSnapshotResult(value, error));
    }

    private void handleSnapshotResult(QuerySnapshot value, FirebaseFirestoreException error) {
        if (error != null) {
            Toast.makeText(getContext(), "Failed to load products", Toast.LENGTH_SHORT).show();
            return;
        }

        if (value != null) {
            List<ProductModel> data = value.toObjects(ProductModel.class);
            datalist.clear();
            datalist.addAll(data);

            if (adapter == null) {
                GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                adminCategoryRecyclerview.setLayoutManager(layoutManager);
                adapter = new ExploreProductAdapter(getContext(), datalist);
                adminCategoryRecyclerview.setHasFixedSize(true);
                adminCategoryRecyclerview.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void clearData() {
        datalist.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void showOnlySelectedScrollView(HorizontalScrollView visibleScrollView) {
        mobileScrollView.setVisibility(View.GONE);
        earphoneHorizontalScrollView.setVisibility(View.GONE);
        tvHorizontalScrollView.setVisibility(View.GONE);
        laptopHorizontalScrollView.setVisibility(View.GONE);
        headphoneHorizontalScrollView.setVisibility(View.GONE);
        speakerHorizontalScrollView.setVisibility(View.GONE);
        keyboardHorizontalScrollView.setVisibility(View.GONE);
        mouseHorizontalScrollView.setVisibility(View.GONE);
        cameraHorizontalScrollView.setVisibility(View.GONE);
        smartwatchHorizontalScrollView.setVisibility(View.GONE);
        tabletHorizontalScrollView.setVisibility(View.GONE);

        visibleScrollView.setVisibility(View.VISIBLE);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message + " Clicked", Toast.LENGTH_SHORT).show();
    }

    private void selectIndicator(View indicator) {
        indicator.setVisibility(View.VISIBLE);

        mobileIndicator.setVisibility(View.GONE);
        earbudsIndicator.setVisibility(View.GONE);
        tvIndicator.setVisibility(View.GONE);
        laptopsIndicator.setVisibility(View.GONE);
        headphonesIndicator.setVisibility(View.GONE);
        speakersIndicator.setVisibility(View.GONE);
        keyboardIndicator.setVisibility(View.GONE);
        mouseIndicator.setVisibility(View.GONE);
        cameraIndicator.setVisibility(View.GONE);
        smartwatchesIndicator.setVisibility(View.GONE);
        tabletsIndicator.setVisibility(View.GONE);

        indicator.setVisibility(View.VISIBLE);
    }

    // handle onBack press
    private void handleOnBackPress(){
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameLayout, new HomeFragment())
                        .commit();

//                BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
//                bottomNavigationView.setSelectedItemId(R.id.home);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (categoryListener != null) {
            categoryListener.remove();
        }
        if (filteredDataListener != null) {
            filteredDataListener.remove();
        }
    }


}