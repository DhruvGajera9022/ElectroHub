package com.example.swiftmart.Account;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swiftmart.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
	TextView tvPrivacyContent;
	private ImageView backBtn;
	private TextView toolBarTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);

		// Toolbar Elements
		backBtn = findViewById(R.id.backBtn);
		toolBarTitle = findViewById(R.id.toolBarTitle);
		toolBarTitle.setText(R.string.privacy_policy);
		backBtn.setOnClickListener(v -> onBackPressed());

		// Privacy Policy Content
		tvPrivacyContent = findViewById(R.id.tvPrivacyContent);

		String privacyText = "<b>📜 Privacy Policy</b><br><br>" +
				                     "<b>Effective Date:</b> March 29, 2025<br><br>" +
				                     "Welcome to <b>ElectroHub</b>! Your privacy is our top priority. This Privacy Policy explains how we collect, use, and protect your data while you use our app.<br><br>" +

				                     "<b>🔹 1. Information We Collect</b><br>" +
				                     "We may collect the following types of information:<br>" +
				                     "✔ <b>Personal Information:</b> Name, email, phone number, shipping address.<br>" +
				                     "✔ <b>Usage Data:</b> Features you interact with, shopping history, search history.<br>" +
				                     "✔ <b>Device Information:</b> Device model, operating system, IP address, network type.<br>" +
				                     "✔ <b>Payment Information:</b> Payment methods, transaction history (we do not store credit card details).<br>" +
				                     "✔ <b>Location Data:</b> If enabled, we may collect your location to suggest nearby deals.<br><br>" +

				                     "<b>🔹 2. How We Use Your Information</b><br>" +
				                     "We use your data to:<br>" +
				                     "✔ <b>Provide a seamless shopping experience.</b><br>" +
				                     "✔ <b>Improve app performance</b> and personalize recommendations.<br>" +
				                     "✔ <b>Process orders and payments</b> securely.<br>" +
				                     "✔ <b>Send notifications</b> about offers, promotions, and order status.<br>" +
				                     "✔ <b>Prevent fraudulent activities</b> and enhance security.<br><br>" +

				                     "<b>🔹 3. Data Protection & Security</b><br>" +
				                     "We implement <b>industry-standard security</b> measures to protect your data, including:<br>" +
				                     "✔ Encryption to secure sensitive information.<br>" +
				                     "✔ Two-factor authentication for additional security.<br>" +
				                     "✔ Firewalls and intrusion detection systems.<br>" +
				                     "✔ Regular security audits and vulnerability checks.<br>" +
				                     "However, no system is 100% secure, so we recommend keeping your credentials private.<br><br>" +

				                     "<b>🔹 4. Your Rights & Choices</b><br>" +
				                     "✔ <b>Access & Control:</b> You can request access, update, or delete your personal data.<br>" +
				                     "✔ <b>Opt-out Options:</b> You can manage notification settings and opt out of marketing emails.<br>" +
				                     "✔ <b>Cookies & Tracking:</b> You can disable cookies from your device settings if preferred.<br>" +
				                     "✔ <b>Data Deletion:</b> You can request account deletion at any time.<br><br>" +

				                     "<b>🔹 5. Third-Party Services</b><br>" +
				                     "We may share your data with <b>trusted partners</b> such as:<br>" +
				                     "✔ Payment processors (PayPal, Stripe) for secure transactions.<br>" +
				                     "✔ Shipping carriers (FedEx, UPS) for order deliveries.<br>" +
				                     "✔ Analytics providers (Google Analytics) to improve user experience.<br>" +
				                     "We ensure these third parties comply with strict privacy policies.<br><br>" +

				                     "<b>🔹 6. Data Retention</b><br>" +
				                     "We retain your personal information only as long as necessary for legal and business purposes:<br>" +
				                     "✔ Account data is stored until you request deletion.<br>" +
				                     "✔ Transaction records are kept for tax and legal compliance.<br>" +
				                     "✔ Security logs are stored temporarily to detect threats.<br><br>" +

				                     "<b>🔹 7. Children's Privacy</b><br>" +
				                     "ElectroHub is not intended for users under the age of 13. We do not knowingly collect data from children.<br><br>" +

				                     "<b>🔹 8. Policy Updates</b><br>" +
				                     "We may update this policy periodically. Any changes will be reflected on this page, and you’ll be notified of major updates.<br><br>" +

				                     "<b>🔹 9. Contact Us</b><br>" +
				                     "📧 <b>Email:</b> support@electrohub.com<br>" +
				                     "🌐 <b>Website:</b> www.electrohub.com<br>" +
				                     "📍 <b>Follow Us:</b> Facebook | Instagram | Twitter<br><br>" +

				                     "Thank you for trusting <b>ElectroHub</b>! ⚡<br>";

		tvPrivacyContent.setText(Html.fromHtml(privacyText));
	}
}
