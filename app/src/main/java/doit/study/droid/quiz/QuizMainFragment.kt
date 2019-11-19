package doit.study.droid.quiz

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import doit.study.droid.R
import doit.study.droid.app.BaseApp
import timber.log.Timber
import javax.inject.Inject

class QuizMainFragment: Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: QuizMainViewModel

    private var pager: ViewPager? = null
    private var pagerAdapter: QuizPagerAdapter? = null
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)[QuizMainViewModel::class.java]
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler = Handler()

        viewModel.items.observe(viewLifecycleOwner, Observer {
            Timber.d("$this result: $it")
            if (it.isNotEmpty()) setupPagerAdapter(it)
        })

        viewModel.updateTitle.observe(viewLifecycleOwner, Observer { questionsLeft ->
            Timber.d("updateQuestionsLeft ")
            activity?.apply {
                if (questionsLeft == 0)
                    title = getString(R.string.test_completed)
                else
                    title = resources.getQuantityString(
                            R.plurals.numberOfQuestionsInTest,
                            questionsLeft,
                            questionsLeft
                    )
            }
        })

        viewModel.addResultPageAndSwipeOnce.observe(viewLifecycleOwner, Observer {
            pagerAdapter?.addResultPage()
            it.getContentIfNotHandled()?.let {
                handler?.postDelayed({
                    pagerAdapter?.addResultPage()
                    pager?.setCurrentItem(viewModel.items.value?.size ?: 0, true)
                }, 2000)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        handler?.removeCallbacksAndMessages(null)
    }

    private fun setupPagerAdapter(items: List<QuizView>) {
        pagerAdapter = QuizPagerAdapter(childFragmentManager, items, activity!!)
        pager?.adapter = pagerAdapter
        pagerAdapter?.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager = view.findViewById(R.id.view_pager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_interrogator, container, false)
    }

    companion object {
        fun newInstance(): QuizMainFragment = QuizMainFragment()
    }

}
