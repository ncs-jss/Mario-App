package com.ncs.marioapp.UI.SettingsScreen.NewChanges

import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView)?.isAppearanceLightStatusBars = false
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        setUpViews()

        val desc = """# Mario Version 2.5 Update! 🚀
           
Mario keeps evolving, and version **2.5** brings powerful new updates to enhance your experience even further. With improved usability, exciting new features, and essential bug fixes, this update ensures that Mario continues to be the perfect career companion for you.
           
### Key Features in Version 2.5:
           
- **Enhanced Review System**

- **Seamless NCS Apps Integration**

- **Advanced Notification System**

- **Refined UI & UX Enhancements**

- **Performance Upgrades & Bug Fixes**

- **Updated Score & Reward System**

- **Expanded Event Management Features**

- **Much More**

### Version Log
           
<table>
              <tr>
                <th>Version</th>
                <th>Release Date</th>
                <th>Features & Updates</th>
              </tr>
              <tr>
                <td>2.5</td>
                <td>16 Feb, 25</td>
                <td>Enhanced Review System, NCS Apps Integration, Advanced Notifications, UI & UX Enhancements, Performance Fixes, Updated Score System, Expanded Event Management.</td>
              </tr>
              <tr>
                <td>2.3</td>
                <td>5 Feb, 25</td>
                <td>Bug Fixes & Improvements. </td>
              </tr>
              <tr>
                <td>2.2</td>
                <td>4 Feb, 25</td>
                <td>Improved Onboarding and Registration, New and Improved Events Pages, New Banners, General Bug Fixes, NCS Nerd Store, New Score Page, and Improved Ticketing Mechanism.</td>
              </tr>
              <tr>
                <td>1.7</td>
                <td>22 Dec, 24</td>
                <td>Bug Fixes & Improvements. </td>
              </tr>
              <tr>
                <td>1.1</td>
                <td>09 Nov, 24</td>
                <td>Initial release with Events, Posts & Story Blogs, Nerd Store Goodies, Mario Score Section, and NCS Integration.</td>
              </tr>
            </table>
           
### Why Choose Mario?
           
- **Designed for Growth**: Every feature is built to support your career progression and help you reach new milestones.
             
- **Exclusive & Curated**: Mario provides access to exclusive events, content, and a professional network dedicated to success.
             
- **Seamless Experience**: With each update, we ensure Mario is faster, smoother, and easier to use, giving you a hassle-free career management platform.
             
### Update Now & Stay Ahead!
           
Upgrade to version **2.5** today and enjoy all the latest features, improvements, and optimizations. Continue leveling up your career with Mario!
           
**More exciting updates coming soon! Stay connected.**
           
**#Mario #CareerGrowth #NCS #LevelUp**
           
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