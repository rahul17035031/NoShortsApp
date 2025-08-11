package com.youtubewebview.app;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "YouTubeWebView";
    private static final String YOUTUBE_URL = "https://www.youtube.com";
    
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Enable full-screen mode and hide system UI
        setupFullScreenMode();
        
        setContentView(R.layout.activity_main);
        
        // Initialize views
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        
        // Setup WebView
        setupWebView();
        
        // Load YouTube
        webView.loadUrl(YOUTUBE_URL);
        
        Log.d(TAG, "MainActivity created and YouTube loading initiated");
    }

    /**
     * Configure full-screen immersive mode
     */
    private void setupFullScreenMode() {
        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        // Hide system UI for immersive experience
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    /**
     * Configure WebView settings and clients
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        
        // Enable JavaScript (required for YouTube)
        webSettings.setJavaScriptEnabled(true);
        
        // Enable DOM storage (required for modern web apps)
        webSettings.setDomStorageEnabled(true);
        
        // Enable database storage
        webSettings.setDatabaseEnabled(true);
        
        // Enable application cache
        webSettings.setAppCacheEnabled(true);
        
        // Set user agent to desktop to get full YouTube experience
        webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        
        // Enable media playback
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        
        // Enable loading images
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        
        // Enable zoom controls but hide zoom buttons
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        
        // Enable wide viewport
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        
        // Mixed content mode
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        
        // Set custom WebViewClient to intercept navigation and inject JavaScript
        webView.setWebViewClient(new CustomWebViewClient());
        
        // Set WebChromeClient for better media support and progress tracking
        webView.setWebChromeClient(new CustomWebChromeClient());
        
        Log.d(TAG, "WebView configured with optimized settings");
    }

    /**
     * Custom WebViewClient to handle URL interception and JavaScript injection
     */
    private class CustomWebViewClient extends WebViewClient {
        
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            Log.d(TAG, "Navigation intercepted: " + url);
            
            // Intercept Shorts URLs and redirect to regular watch URLs
            String redirectedUrl = redirectShortsToWatch(url);
            if (!redirectedUrl.equals(url)) {
                Log.d(TAG, "Redirecting Shorts URL: " + url + " -> " + redirectedUrl);
                view.loadUrl(redirectedUrl);
                return true; // We handle this URL
            }
            
            // Allow normal YouTube navigation
            if (url.contains("youtube.com") || url.contains("youtu.be")) {
                return false; // Let WebView handle it
            }
            
            // Block external navigation outside YouTube
            Log.d(TAG, "Blocking external navigation to: " + url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "Page loading started: " + url);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "Page loading finished: " + url);
            
            // Hide loading indicator
            progressBar.setVisibility(View.GONE);
            
            // Inject JavaScript to hide Shorts-related elements
            injectShortsRemovalScript(view);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e(TAG, "WebView error: " + description + " (Code: " + errorCode + ")");
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Custom WebChromeClient for enhanced media support
     */
    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            // You can add a progress bar update here if needed
            Log.d(TAG, "Loading progress: " + newProgress + "%");
        }
    }

    /**
     * Convert YouTube Shorts URLs to regular watch URLs
     * Example: https://www.youtube.com/shorts/ABC123 -> https://www.youtube.com/watch?v=ABC123
     */
    private String redirectShortsToWatch(String url) {
        if (url.contains("/shorts/")) {
            // Extract video ID from shorts URL
            String[] parts = url.split("/shorts/");
            if (parts.length > 1) {
                String videoId = parts[1].split("[?&#]")[0]; // Get video ID before any query params
                String baseUrl = parts[0].replace("/shorts", "");
                // Ensure we're using the main YouTube domain
                if (!baseUrl.contains("youtube.com")) {
                    baseUrl = "https://www.youtube.com";
                }
                return baseUrl + "/watch?v=" + videoId;
            }
        }
        return url; // Return original URL if not a shorts URL
    }

    /**
     * Inject JavaScript to hide Shorts-related UI elements
     */
    private void injectShortsRemovalScript(WebView webView) {
        String script = """
            (function() {
                console.log('YouTube Shorts removal script starting...');
                
                // Function to remove Shorts elements
                function removeShortsElements() {
                    // Remove Shorts shelf/section
                    var shortsShelf = document.querySelector('ytd-rich-shelf-renderer[is-shorts]');
                    if (shortsShelf) {
                        shortsShelf.style.display = 'none';
                        console.log('Removed Shorts shelf');
                    }
                    
                    // Remove Shorts section
                    var shortsSections = document.querySelectorAll('[aria-label*="Shorts"], [title*="Shorts"], [href*="/shorts"]');
                    shortsSections.forEach(function(element) {
                        element.style.display = 'none';
                        console.log('Removed Shorts element');
                    });
                    
                    // Remove Shorts navigation tab
                    var shortsTab = document.querySelector('a[href="/shorts"]');
                    if (shortsTab) {
                        shortsTab.style.display = 'none';
                        console.log('Removed Shorts navigation tab');
                    }
                    
                    // Remove Shorts button in mobile view
                    var shortsButtons = document.querySelectorAll('yt-icon[icon="yt-icons:shorts"], [aria-label="Shorts"]');
                    shortsButtons.forEach(function(button) {
                        var parent = button.closest('a, button, [role="button"]');
                        if (parent && parent.href && parent.href.includes('/shorts')) {
                            parent.style.display = 'none';
                            console.log('Removed Shorts button');
                        }
                    });
                    
                    // Remove Shorts links in suggestions
                    var shortsLinks = document.querySelectorAll('a[href*="/shorts/"]');
                    shortsLinks.forEach(function(link) {
                        link.style.display = 'none';
                        console.log('Removed Shorts link');
                    });
                    
                    // Hide Shorts-related text
                    var elementsWithShortsText = document.querySelectorAll('*');
                    elementsWithShortsText.forEach(function(element) {
                        if (element.textContent && element.textContent.trim() === 'Shorts' && 
                            element.children.length === 0) {
                            var container = element.closest('a, button, [role="button"]');
                            if (container) {
                                container.style.display = 'none';
                                console.log('Removed Shorts text element');
                            }
                        }
                    });
                }
                
                // Initial removal
                removeShortsElements();
                
                // Create observer to handle dynamically loaded content
                var observer = new MutationObserver(function(mutations) {
                    mutations.forEach(function(mutation) {
                        if (mutation.addedNodes.length > 0) {
                            removeShortsElements();
                        }
                    });
                });
                
                // Start observing
                observer.observe(document.body, {
                    childList: true,
                    subtree: true
                });
                
                console.log('YouTube Shorts removal script initialized');
                
                // Re-run every 2 seconds to catch any missed elements
                setInterval(removeShortsElements, 2000);
            })();
            """;
        
        webView.evaluateJavascript(script, null);
        Log.d(TAG, "Shorts removal JavaScript injected");
    }

    @Override
    public void onBackPressed() {
        // Handle back navigation in WebView
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        // Re-apply full screen mode
        setupFullScreenMode();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}
