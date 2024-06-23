package com.nasyithm.dicostoryv2.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nasyithm.dicostoryv2.R
import com.nasyithm.dicostoryv2.data.remote.response.ListStoryItem
import com.nasyithm.dicostoryv2.databinding.ActivityMainBinding
import com.nasyithm.dicostoryv2.view.ViewModelFactory
import com.nasyithm.dicostoryv2.view.auth.login.LoginActivity
import com.nasyithm.dicostoryv2.view.story.add.AddStoryActivity
import com.nasyithm.dicostoryv2.view.story.detail.StoryDetailActivity
import com.nasyithm.dicostoryv2.view.story.maps.StoriesMapsActivity

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvStories.layoutManager = layoutManager

        mainViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        showStoriesMaps()
        changeLanguage()
        logout()
        getStoriesData()
        swipeToRefresh()
        showAddStory()
        playAnimation()
    }

    private fun showStoriesMaps() {
        binding.btnStoriesMaps.setOnClickListener {
            val showStoriesMapsIntent = Intent(
                this@MainActivity, StoriesMapsActivity::class.java
            )
            startActivity(showStoriesMapsIntent)
        }
    }

    private fun changeLanguage() {
        binding.btnChangeLanguage.setOnClickListener {
            val changeLanguageIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(changeLanguageIntent)
        }
    }

    private fun logout() {
        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(getString(R.string.logout))
                setMessage(getString(R.string.confirm_logout))
                setNegativeButton(getString(R.string.no)) { _, _ -> }
                setPositiveButton(getString(R.string.yes)) { _, _ ->
                    mainViewModel.logout()
                }
                create()
                show()
            }
        }
    }

    private fun getStoriesData() {
        val adapter = StoriesAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        mainViewModel.storiesData.observe(this) {
            adapter.submitData(lifecycle, it)
        }

        adapter.setOnItemClickCallback(object : StoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                showStoryDetail(data.id.toString())
            }
        })
    }

    private fun swipeToRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            val adapter = StoriesAdapter()
            mainViewModel.storiesData.observe(this) {
                adapter.submitData(lifecycle, it)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showStoryDetail(storyId: String) {
        val showStoryDetailIntent = Intent(
            this@MainActivity, StoryDetailActivity::class.java
        )
        showStoryDetailIntent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, storyId)
        startActivity(showStoryDetailIntent)
    }

    private fun showAddStory() {
        binding.btnAddStory.setOnClickListener {
            val addStoryIntent = Intent(
                this@MainActivity, AddStoryActivity::class.java
            )
            startActivity(addStoryIntent)
        }
    }

    private fun playAnimation() {
        val tvTitle = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(200)
        val btnStoriesMaps = ObjectAnimator.ofFloat(binding.btnStoriesMaps, View.ALPHA, 1f).setDuration(200)
        val btnLanguage = ObjectAnimator.ofFloat(binding.btnChangeLanguage, View.ALPHA, 1f).setDuration(200)
        val btnLogout = ObjectAnimator.ofFloat(binding.btnLogout, View.ALPHA, 1f).setDuration(200)
        val swipeRefresh = ObjectAnimator.ofFloat(binding.swipeRefreshLayout, View.ALPHA, 1f).setDuration(200)
        val btnAddStory = ObjectAnimator.ofFloat(binding.btnAddStory, View.ALPHA, 1f).setDuration(200)

        AnimatorSet().apply {
            playSequentially(tvTitle, btnStoriesMaps, btnLanguage, btnLogout, swipeRefresh, btnAddStory)
            startDelay = 100
        }.start()
    }
}