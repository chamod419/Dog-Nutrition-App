package com.example.happydog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class CardDetailsDialog extends Dialog {

    private EditText cardNumberEditText;
    private EditText cardExpiryEditText;
    private EditText cardCvcEditText;
    private Button submitButton;
    private OnCardDetailsEnteredListener listener;

    public CardDetailsDialog(Context context, OnCardDetailsEnteredListener listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_card_details);

        cardNumberEditText = findViewById(R.id.cardNumberEditText);
        cardExpiryEditText = findViewById(R.id.cardExpiryEditText);
        cardCvcEditText = findViewById(R.id.cardCvcEditText);
        submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String cardNumber = cardNumberEditText.getText().toString();
            String cardExpiry = cardExpiryEditText.getText().toString();
            String cardCvc = cardCvcEditText.getText().toString();

            if (listener != null) {
                listener.onCardDetailsEntered(cardNumber, cardExpiry, cardCvc);
            }
            dismiss();
        });
    }

    public interface OnCardDetailsEnteredListener {
        void onCardDetailsEntered(String cardNumber, String cardExpiry, String cardCvc);
    }
}
