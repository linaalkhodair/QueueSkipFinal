package com.example.queueskip.ui.dashboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.queueskip.Adapter.CartAdapter;
import com.example.queueskip.CheckoutActivity;
import com.example.queueskip.Database.ModelDB.Cart;
import com.example.queueskip.R;
import com.example.queueskip.utliz.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DashboardFragment extends Fragment {
//private Button cartBut;
//private TextView testText;
    private ElegantNumberButton elegantNumberButton;
    RecyclerView recycler_cart;
    public static Button btn_place_order;
    TextView total;
    CompositeDisposable compositionDisposable;
    Button clear_btn;
    CartAdapter cartAdapter;
    public  static int totalAmount = 0;
    private Button checkoutBtnDialog;
    private TextView totalPriceDialog;
    private Button closeBtn;
    private Button okBtn, cancelBtn;
    private TextView dialogMsg;

    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        elegantNumberButton=view.findViewById(R.id.txt_amount);

//        cartBut = (Button)view.findViewById(R.id.cartAct); //new new new added
//        testText = view.findViewById(R.id.testText); //??new new added
//
//        cartBut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getContext(), cartActivity.class));
//            }
//        });
        // final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        compositionDisposable = new CompositeDisposable();
        recycler_cart = (RecyclerView) view.findViewById( R.id.recycler_cart );
        recycler_cart.setLayoutManager( new LinearLayoutManager( getContext() ) );
        recycler_cart.setHasFixedSize( true );

       // total= view.findViewById(R.id.total);




        btn_place_order = (Button) view.findViewById( R.id.btn_place_order );
        loadCartItems(); //it was a comment!!!!!
        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialog();

              //  startActivity(new Intent(getContext(), CheckoutActivity.class));

            }
        });

        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.logout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        okBtn=dialog.findViewById(R.id.ok_btn_dialog);
        cancelBtn=dialog.findViewById(R.id.cancel_btn_dialog);
        dialogMsg=dialog.findViewById(R.id.dialog_message);
        dialogMsg.setText("Are you sure you want to clear cart?");
        cancelBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             dialog.cancel();

                                         }//end of onClick
                                     }//end of OnClickListener
        );

        clear_btn=(Button) view.findViewById( R.id.clear );
        clear_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cartAdapter.clear();
                        btn_place_order.setText("Checkout"+"     Total price: "+0+"SR");
                        dialog.cancel();
                    }
                });


                // then reload the data

            }
        } );
        return view;
    }

    private void createDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.checkout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        checkoutBtnDialog=dialog.findViewById(R.id.checkout_btn_dialog);
        totalPriceDialog=dialog.findViewById(R.id.checkout_dialog_amount);
        closeBtn=dialog.findViewById(R.id.button_close);

        totalPriceDialog.setText("Total price: "+totalAmount+" SR");

        dialog.show();

        checkoutBtnDialog.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View view) {
                                         startActivity(
                                                 new Intent(getContext(), CheckoutActivity.class));
                                         cartAdapter.clear();
                                     }//end of onClick
                                 }//end of OnClickListener
        );
        closeBtn.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View view) {
                                             dialog.cancel();

                                         }//end of onClick
                                     }//end of OnClickListener
        );

    }

    @Override
    public void onStop() {
        compositionDisposable.clear();
        super.onStop();
    }


    @Override
    public void onDestroy() {
        compositionDisposable.clear();
        super.onDestroy();
    }
    private void loadCartItems() {

        compositionDisposable.add(

                Common.cartRepository.getCartItems().observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io() )
                        .subscribe( new Consumer<List<Cart>>() {
                            @Override
                            public void accept(List<Cart> carts) {
                                displayCartItem(carts);
                            }
                        } )



        );


    }

    private void displayCartItem(List<Cart> carts){
        cartAdapter=new CartAdapter(getContext(),carts);
        recycler_cart.setAdapter(cartAdapter);
        totalAmount(carts);
    }

    private void totalAmount(List<Cart> cartList){
//        int totalAmount = 0;
        int price;
        int amount;

        for(int i=0; i<cartList.size(); i++){
            //elegantNumberButton.setNumber(String.valueOf(cartList.get(i).amount));

            price= cartList.get(i).Price;
            amount= cartList.get(i).amount;
            totalAmount+=  price*amount;
        }
        btn_place_order.setText("Check out"+"     Total price: "+totalAmount+"SR");

       // total.setText(""+totalAmount);
        //return totalAmount;
    }



}