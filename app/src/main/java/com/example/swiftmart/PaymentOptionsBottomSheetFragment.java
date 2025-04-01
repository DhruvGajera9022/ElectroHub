package com.example.swiftmart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class PaymentOptionsBottomSheetFragment extends DialogFragment {
	private RadioGroup paymentRadioGroup;
	private RadioButton onlinePaymentRadioButton, cashOnDeliveryRadioButton;
	private OnPaymentOptionSelectedListener mListener;

	// Interface to communicate with the parent activity
	public interface OnPaymentOptionSelectedListener {
		void onPaymentOptionSelected(boolean isOnlinePayment);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_payment_options, container, false);

		paymentRadioGroup = view.findViewById(R.id.payment_radio_group);
		onlinePaymentRadioButton = view.findViewById(R.id.online_payment_radio_button);
		cashOnDeliveryRadioButton = view.findViewById(R.id.cash_on_delivery_radio_button);

		// Set default option to online payment
		onlinePaymentRadioButton.setChecked(true);

		view.findViewById(R.id.confirm_payment_button).setOnClickListener(v -> {
			// Determine selected payment option
			boolean isOnlinePayment = onlinePaymentRadioButton.isChecked();
			if (mListener != null) {
				mListener.onPaymentOptionSelected(isOnlinePayment);
			}
			dismiss();
		});

		return view;
	}

	// Set listener to communicate with the activity
	public void setOnPaymentOptionSelectedListener(OnPaymentOptionSelectedListener listener) {
		mListener = listener;
	}
}
