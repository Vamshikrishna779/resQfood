package com.example.resqfood;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BlogActivity extends AppCompatActivity {

    private TextView textViewLearnMore1;
    private TextView textViewLearnMore2;
    private TextView textViewLearnMore3;
    private TextView textViewLearnMore4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        textViewLearnMore1 = findViewById(R.id.textViewLearnMore1);
        textViewLearnMore2 = findViewById(R.id.textViewLearnMore2);
        textViewLearnMore3 = findViewById(R.id.textViewLearnMore3);
        textViewLearnMore4 = findViewById(R.id.textViewLearnMore4);

        setClickableSpan(textViewLearnMore1, "https://www.bookeventz.com/blog/donate-food-leftover-ngo");
        setClickableSpan(textViewLearnMore2, "https://akshayapatra.medium.com/making-small-donations-towards-ngos-make-bigger-impacts-e6ad7803efd9");
        setClickableSpan(textViewLearnMore3, "https://www.narayanseva.org/the-importance-of-donating-for-food-and-helping-those-in-need/");
        setClickableSpan(textViewLearnMore4, "https://www.foodbank.org.au/support-us/");
    }

    private void setClickableSpan(TextView textView, final String url) {
        SpannableString spannableString = new SpannableString("Learn More");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Handle click event (e.g., open URL)
                openUrl(url);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(BlogActivity.this, com.google.android.libraries.places.R.color.quantum_googred));
                ds.setUnderlineText(false); // Remove underline
            }
        };
        spannableString.setSpan(clickableSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
