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
           ## O2 New Update : Maximizing Your Project Potential 🚀🌔
           ### Release Notes 24.6.17 - [17/06/2024] 📝

           ---

           ![img](https://i.giphy.com/media/v1.Y2lkPTc5MGI3NjExMzBxcDJ2NmlwNmY5MjNrMDY0eXh1aWpvd3ZqOWdyajl1YWVqZDUzdiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/8vIFoKU8s4m4CBqCao/giphy.gif)
           ## New features, Minor bug fixes, general UI and performance improvements. ⚙️


           ### New Features 🚀
           - **Organisations:** Organisation can be setup and Projects are now moved under the organisation, independent projects are now deprecated.
           - **State to list Mapper feature :** Now mappings can be made for Task Status to List Names so to automate shifiting of a task from one list to another if task status is change and viceversa.

           ### Enhancements ✨

           - **Task Drafting Enhancements:** Improvements in saving the tasks as drafts in case of unexpected failures so that the progress is not lost.
           
           - **Improvements for moderators:** Now easily keep track of the tasks you are moderating, UI improvements.
           
           - **Checklists creation:** Moderators can add more checklists after the task has been created.
           
           - **Task summary:** Task summary can now be edited after the task has been created.
           
           - **Checklist Progress:** Progress of a Task's checklist can be seen directly from task detail page. 

           ### Performance Improvements ⏫

           - Improved opening of the screens and reduced load times.
           - Imrpoved app's navigation smoothness.

           ### User Interface Changes ✦

           - Revamped UI of user's workspace.
           - Made general improvements in UX.
           - Chats UI improved.

           ### Feedback ✅

           - Feedback is the key to enhancing user experience, it is our appeal to the users for providing any constructive feedback about any issues faced by them. 
           - Feedback can be shared by shaking the device 3 times if Shake to Report is active or by going to settings to share feedback or bugs.


        
           -- Team Oxygen 💙

           ---

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