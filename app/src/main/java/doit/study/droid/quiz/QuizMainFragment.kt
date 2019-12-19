package doit.study.droid.quiz

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import doit.study.droid.app.BaseApp
import doit.study.droid.databinding.FragmentQuizMainBinding
import doit.study.droid.utils.lazyAndroid
import timber.log.Timber
import javax.inject.Inject

class QuizMainFragment: Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: QuizMainViewModel by lazyAndroid {
        ViewModelProviders.of(this, viewModelFactory)[QuizMainViewModel::class.java]
    }
    private lateinit var viewDataBinding: FragmentQuizMainBinding
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentQuizMainBinding.inflate(inflater, container, false)
        return viewDataBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.apply {
            lifecycleOwner = viewLifecycleOwner
        }

        setupPagerAdapter()
        setupActionBarTitle()
        setupResultPage()
        setupNavigationToResultPage()
        viewDataBinding.titlePagerTabStrip.tabIndicatorColor = Color.BLACK
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }

    private fun setupNavigationToResultPage() {
        viewModel.swipeToResultPageEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                handler.postDelayed({
                    viewDataBinding.viewPager.setCurrentItem(it, true)
                }, DELAY_NAV_TO_RESULT_PAGE_MS)
            }
        })
    }

    private fun setupResultPage() {
        viewModel.addResultPageEvent.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                viewDataBinding.viewPager?.adapter?.notifyDataSetChanged()
            }
        })
    }

    private fun setupActionBarTitle() {
        viewModel.actionBarTitle.observe(viewLifecycleOwner, Observer {
            activity?.title = it
        })
    }

    private fun setupPagerAdapter() {
        viewModel.items.observe(viewLifecycleOwner, Observer {
            Timber.d("$this result: $it")
            if (it.isNotEmpty()) {
                viewDataBinding.viewPager.apply {
                    adapter = QuizPagerAdapter(childFragmentManager, viewModel)
                    adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    companion object {
        fun newInstance(): QuizMainFragment = QuizMainFragment()
        private const val DELAY_NAV_TO_RESULT_PAGE_MS = 2_000L
    }

}
