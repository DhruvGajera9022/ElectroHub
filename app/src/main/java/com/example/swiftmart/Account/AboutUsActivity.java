package com.example.swiftmart.Account;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftmart.R;

public class AboutUsActivity extends AppCompatActivity {
	TextView tvAboutContent, tvHeader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_us);

		tvHeader = findViewById(R.id.tvHeader);
		tvAboutContent = findViewById(R.id.tvAboutContent);

		tvHeader.setText("About Us");

		String aboutText = "Welcome to ElectroHub – your one-stop destination for all things electronic!\n\n" +
				                   "At ElectroHub, we bring you a vast collection of the latest and most advanced electronic devices, including " +
				                   "smartphones, earbuds, televisions, tablets, computer accessories like mice and keyboards, laptops, " +
				                   "headphones, speakers, cameras, and smartwatches. Whether you’re looking for cutting-edge technology " +
				                   "or budget-friendly options, we’ve got you covered.\n\n" +

				                   "★ Our Mission ★\n" +
				                   "✔ Provide a seamless and secure shopping experience.\n" +
				                   "✔ Offer a wide range of premium brands to meet every customer’s needs.\n" +
				                   "✔ Ensure high-quality products with verified authenticity.\n" +
				                   "✔ Deliver fast and reliable service, making online shopping hassle-free.\n\n" +

				                   "★ Our Products & Brands ★\n" +
				                   "📱 Smartphones & Tablets: Apple, Samsung, OnePlus, Xiaomi, Realme, Oppo, Vivo, Google Pixel.\n" +
				                   "💻 Laptops & Accessories: Dell, HP, Lenovo, ASUS, Acer, Apple MacBook.\n" +
				                   "🎧 Audio Devices: Sony, Bose, JBL, Boat, Noise, Sennheiser, Skullcandy.\n" +
				                   "⌚ Smartwatches & Wearables: Apple Watch, Samsung Galaxy Watch, Fitbit, Amazfit, Garmin.\n" +
				                   "📷 Cameras & Photography: Canon, Nikon, Sony, GoPro, Fujifilm.\n" +
				                   "📺 TVs & Entertainment: LG, Sony, Samsung, TCL, MI, OnePlus, Hisense.\n" +
				                   "⌨ Computer Peripherals: Logitech, Razer, Corsair, HyperX, SteelSeries.\n\n" +

				                   "★ Why Choose ElectroHub? ★\n" +
				                   "✔ Authenticity Guaranteed: 100% genuine products from trusted brands.\n" +
				                   "✔ Competitive Pricing: Best deals and discounts on top products.\n" +
				                   "✔ Customer Support: A dedicated team to assist you with any queries.\n" +
				                   "✔ Secure Transactions: Safe and encrypted payment methods.\n\n" +

				                   "★ Contact Us ★\n" +
				                   "📧 Email: support@electrohub.com\n" +
				                   "🌐 Website: www.electrohub.com\n" +
				                   "📍 Follow Us: Facebook | Instagram | Twitter\n\n" +

				                   "Thank you for choosing ElectroHub – where technology meets convenience! 🚀";

		tvAboutContent.setText(aboutText);
	}
}
