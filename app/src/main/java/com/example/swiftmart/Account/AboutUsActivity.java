package com.example.swiftmart.Account;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.swiftmart.R;

public class AboutUsActivity extends AppCompatActivity {
	TextView tvAboutContent;
	private ImageView backBtn;
	private TextView toolBarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		// Toolbar Elements
		backBtn = findViewById(R.id.backBtn);
		toolBarTitle = findViewById(R.id.toolBarTitle);
		toolBarTitle.setText("About Us");
		backBtn.setOnClickListener(v -> onBackPressed());

		// About Us Content
		tvAboutContent = findViewById(R.id.tvAboutContent);

		String aboutText = "<b>🚀 Welcome to ElectroHub – Your Ultimate Electronics Store!</b><br><br>" +
				                   "ElectroHub is your one-stop destination for cutting-edge technology, offering a vast collection of top-quality electronic devices. Whether you're a tech enthusiast or a casual user, we've got everything to meet your digital needs.<br><br>" +

				                   "<b>🌟 Our Mission</b><br>" +
				                   "✔ Deliver a seamless and secure shopping experience.<br>" +
				                   "✔ Provide a wide range of premium brands to cater to every customer.<br>" +
				                   "✔ Ensure authenticity and quality with trusted sellers.<br>" +
				                   "✔ Offer fast and reliable service to make shopping hassle-free.<br><br>" +

				                   "<b>🛒 Our Products & Brands</b><br>" +
				                   "📱 <b>Smartphones & Tablets:</b> Apple, Samsung, OnePlus, Xiaomi, Google Pixel.<br>" +
				                   "💻 <b>Laptops & Accessories:</b> Dell, HP, Lenovo, ASUS, MacBook.<br>" +
				                   "🎧 <b>Audio Devices:</b> Sony, Bose, JBL, Boat, Sennheiser.<br>" +
				                   "⌚ <b>Smartwatches:</b> Apple Watch, Samsung Galaxy Watch, Fitbit, Amazfit.<br>" +
				                   "📷 <b>Cameras:</b> Canon, Nikon, Sony, GoPro, Fujifilm.<br>" +
				                   "📺 <b>TVs & Entertainment:</b> LG, Sony, Samsung, OnePlus, MI.<br>" +
				                   "⌨ <b>Computer Peripherals:</b> Logitech, Razer, HyperX, Corsair.<br><br>" +

				                   "<b>💡 Why Choose ElectroHub?</b><br>" +
				                   "✔ <b>Authenticity Guaranteed:</b> 100% genuine products.<br>" +
				                   "✔ <b>Best Prices & Discounts:</b> Competitive pricing on all items.<br>" +
				                   "✔ <b>Customer Support:</b> A dedicated team to assist you.<br>" +
				                   "✔ <b>Secure Transactions:</b> Safe and encrypted payment options.<br><br>" +

				                   "<b>📞 Contact Us</b><br>" +
				                   "📧 <b>Email:</b> support@electrohub.com<br>" +
				                   "🌐 <b>Website:</b> www.electrohub.com<br>" +
				                   "📍 <b>Follow Us:</b> Facebook | Instagram | Twitter<br><br>" +

				                   "Thank you for choosing <b>ElectroHub</b> – Where Technology Meets Convenience! 🚀";

		tvAboutContent.setText(Html.fromHtml(aboutText));
	}
}
