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
           # Mario is born. ✨
           
           

           **Mario** is here to take your career journey to the next level! This is the **first version** of Mario, designed to offer you a unique and tailored experience for growth and success. Whether you're just starting your career or looking to level up your professional journey, Mario has everything you need to accelerate your path.

           ### What is Mario?

           Mario is a career companion that brings exclusive access to events, insightful content, and exciting opportunities—all within a fun and motivating platform. Developed by NCS, this app is focused on providing ambitious individuals like you with the tools to succeed. From curated **events** to personalized **career progress tracking**, Mario is here to guide you every step of the way.

           ### Key Features:

           - **Events**: Stay engaged with a variety of curated events, from career development workshops to industry-specific meetups. These events are designed to connect you with professionals, build valuable skills, and open doors to new opportunities.
             
           - **Posts & Story Blogs**: Get inspired by posts, career tips, and success stories from professionals across different industries. Learn about the experiences of those who’ve made it and get actionable insights to boost your career.

           - **Nerd Store Goodies**: Who doesn’t love a bit of fun while you level up? With Mario, you get access to exclusive merchandise in the **Nerd Store**, featuring everything from gadgets to career-related goodies that keep you motivated and connected with the Mario community.

           - **Mario Score Section**: Track your personal progress with the **Mario Score**! This feature helps you visualize your growth in real-time, so you can see where you stand in terms of skill development, networking, and career readiness.

           - **Everything from NCS**: We’ve integrated all of NCS’s fantastic resources into the app to give you everything you need in one place. From career resources to professional tools, Mario connects you to the vast NCS ecosystem, empowering you to make the most of every opportunity.

           ### What's Next?

           This is just the start. **Mario** is continuously evolving, and our **next major update** will be introducing **Internship Options**, giving you access to a range of exclusive internships with top companies. Whether you’re looking for your first professional experience or seeking to deepen your expertise, Mario will help you unlock valuable internship opportunities that will propel your career forward.

           The Mario experience isn’t just about features—it’s about building a **community** of like-minded professionals who are dedicated to their growth. By joining Mario, you’ll become part of a network that motivates and supports each other as we all level up together.

           ### Version Log

           | **Version** | **Release Date**  | **Features & Updates**                                  |
           |-------------|-------------------|---------------------------------------------------------|
           | 1.6         | November 2024     | Initial release with **Events**, **Posts & Story Blogs**, **Nerd Store Goodies**, **Mario Score Section**, and **NCS Integration**. |
           | 1.1         | Coming Soon       | **Internship Options** to unlock exclusive internship opportunities from top companies. |

           ### Why Choose Mario?

           - **Tailored for You**: Mario isn’t a one-size-fits-all platform. It adapts to your needs, helping you grow at your own pace. Whether you want to improve your skills, expand your network, or track your progress, Mario has you covered.
             
           - **Exclusive Access**: With Mario’s **Invite-Only Recruitment System**, only the most dedicated individuals are granted access to the platform, ensuring a high-quality, professional network that supports your journey.

           - **Growth-Oriented**: Every feature of Mario is designed with your professional development in mind. From skill-building events to progress tracking, everything is centered around helping you grow and succeed.

           ### Ready to Level Up?

           If you're ready to accelerate your career and be a part of a dedicated community, **download Mario** now and start your journey. Whether you're looking to learn, connect, or unlock new career opportunities, Mario is here to help you succeed.

           **Stay tuned for our next update**—internships are just around the corner!

           **#Mario #LevelUp #CareerGrowth #NCS**
        
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