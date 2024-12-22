package com.ncs.marioapp.UI.SettingsScreen.NewChanges

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import br.tiagohm.markdownview.css.InternalStyleSheet
import br.tiagohm.markdownview.css.styles.Github
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.databinding.ActivityNewChangesBinding


class NewChanges : AppCompatActivity() {

    private lateinit var binding: ActivityNewChangesBinding
    var type: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewChangesBinding.inflate(layoutInflater)

        setUpViews()

        val desc = """
           # Mario Version 1.7 Update! 🚀
           
           

           Mario continues to grow, and version **1.7** is packed with exciting enhancements and features to make your career journey even more seamless and enjoyable. With a focus on improving your experience, we've made significant upgrades to key areas of the app to ensure that Mario continues to be your ultimate career companion.

           ### Key Features in Version 1.7:

           - **Improved Onboarding and Registration**: A brand new and streamlined onboarding process that guides you through setup more intuitively, ensuring a seamless start for new users.
             
           - **New and Improved Events Pages**: Keep everything structured and accessible with the updated events pages. Find and join events effortlessly with a well-organized and enhanced layout.
             
           - **New Banners**: Experience a visually enhanced interface with vibrant and informative banners that keep you updated on important events, posts, and opportunities.
             
           - **General Bug Fixes and Improvements**: We've squashed various bugs and implemented performance improvements to give you a faster and smoother experience.

           - **Addition of NCS Nerd Store**: Redeem exciting goodies from the **Nerd Store** using your Mario coins! Level up with gadgets, merchandise, and more that keep you motivated and engaged.
             
           - **New Score Page**: Track your progress with an all-new score page that now includes a structured transaction history, making it easier to see where you're gaining points and how you’re progressing.
             
           - **Enhanced Ticketing Mechanism**: An improved ticketing mechanism ensures that you can access and manage event tickets more efficiently, enhancing your experience for seamless event participation.
             
           - **Much More**: Additional tweaks and updates throughout the app to enhance usability, improve navigation, and deliver a more refined overall experience.

           ### Version Log

            <table>
              <tr>
                <th>Version</th>
                <th>Release Date</th>
                <th>Features & Updates</th>
              </tr>
              <tr>
                <td>1.7</td>
                <td>25 Dec, 24</td>
                <td>Improved Onboarding and Registration, New and Improved Events Pages, New Banners, General Bug Fixes, NCS Nerd Store, New Score Page, and Improved Ticketing Mechanism.</td>
              </tr>
              <tr>
                <td>1.1</td>
                <td>09 Nov, 24</td>
                <td>Initial release with Events, Posts & Story Blogs, Nerd Store Goodies, Mario Score Section, and NCS Integration.</td>
              </tr>
            </table>


           ### Why Choose Mario?

           - **Tailored for You**: Mario adapts to your unique journey, providing personalized features that help you grow and track progress effectively.
             
           - **Exclusive Access**: Our invite-only system ensures a network of dedicated professionals who are motivated and career-focused, creating a high-quality environment for growth.
             
           - **Growth-Oriented Features**: Everything in Mario is designed to promote professional development, from curated events to detailed progress tracking, all aimed at helping you succeed.
             
           ### Ready to Level Up?

           Update now to experience all the new features and improvements in version **1.7**. Continue your journey, track your growth, redeem exciting rewards, and participate in new and structured events—all within Mario.

           **Stay tuned for future updates** as Mario continues to evolve and bring you even more opportunities for growth!

           **#Mario #Version1_7 #CareerGrowth #NCS #LevelUp**

           -- Team NCS 💙
        """.trimIndent()

        setUpTaskDescription(desc)

        setContentView(binding.root)
    }

    private fun setUpViews() {
        binding.actionBar.btnHam.setImageResource(R.drawable.ic_back_arrow)
        binding.actionBar.btnHam.setOnClickListener {
            onBackPressed()
        }
        binding.actionBar.titleTv.text = "New Changes"
        binding.actionBar.score.gone()
    }

    val script = """
    var allPreTags = document.querySelectorAll('pre');

    allPreTags.forEach(function(preTag) {
      preTag.addEventListener('click', function() {
        var clickedText = preTag.textContent;
        var languageType = preTag.getAttribute('language');
        send.sendCode(clickedText, languageType);
       
      });
    });
    
    var allImgTags = document.querySelectorAll('img');
    var imgArray = [];

    allImgTags.forEach(function(imgTag) {
        if (imgTag.tagName.toLowerCase() === 'img' && imgTag.parentElement.tagName.toLowerCase() !== 'pre') {
        imgTag.addEventListener('click', function() {
            send.sendsingleImage(imgTag.src);
        });
        imgArray.push(imgTag.src);
    }
    });

    send.sendImages(imgArray);
    
"""

    private fun setUpTaskDescription(description: String) {

        val css: InternalStyleSheet = Github()

        with(css) {
            addFontFace(
                "o2font",
                "normal",
                "normal",
                "normal",
                "url('file:///android_res/font/sfregular.ttf')"
            )
            addRule("body", "font-family:o2font")
            addRule("body", "font-size:20px")
            addRule("body", "line-height:28px")
            addRule("body", "background-color: #0B0A0A")
            addRule("body", "color: #fff")
            addRule("body", "padding: 0px 0px 0px 0px")
            addRule("a", "color: #86ff7c")
            addRule("pre", "border: 1px solid #000;")
            addRule("pre", "border-radius: 4px;")
            addRule("pre", "max-height: 400px;")
            addRule("pre", "overflow:auto")
            addRule("pre", "white-space: pre-line")

            addRule("table", "width: 100%")
            addRule("table", "border-collapse: collapse")
            addRule("th, td", "border: 1px solid #fff")
            addRule("th, td", "padding: 8px 12px")
            addRule("th", "background-color: #1a1a1a !important")
            addRule("th", "color: #86ff7c !important")
            addRule("tr:nth-child(even)", "background-color: #2a2a2a !important")
            addRule("tr:nth-child(odd)", "background-color: #1a1a1a !important")
            addRule("td", "background-color: inherit !important")
            addRule("td", "color: #fff !important")


        }

        binding.markdownView.settings.javaScriptEnabled = true
        binding.markdownView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        binding.markdownView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        binding.markdownView.addStyleSheet(css)
        binding.markdownView.addJavascriptInterface(AndroidToJsInterface(), "send")

        binding.markdownView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.evaluateJavascript(script) {}
            }


            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, request?.url)
                startActivity(intent)
                return true
            }

        }

        binding.markdownView.loadMarkdown(description)

        binding.markdownView.visible()

    }


    inner class AndroidToJsInterface {
        @JavascriptInterface
        fun sendCode(codeText: String, language: String?) {

            this@NewChanges.runOnUiThread {

            }
        }


    }


}