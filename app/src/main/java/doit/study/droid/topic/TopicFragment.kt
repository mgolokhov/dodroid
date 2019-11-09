package doit.study.droid.activities

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import doit.study.droid.R
import doit.study.droid.adapters.TopicAdapter
import doit.study.droid.app.BaseApp
import doit.study.droid.quiz.QuizMainFragment
import doit.study.droid.topic.TopicModelView
import doit.study.droid.topic.TopicView
import timber.log.Timber
import javax.inject.Inject

class TopicFragment: Fragment(), SearchView.OnQueryTextListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: TopicModelView

    private var topicAdapter: TopicAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        BaseApp.dagger.inject(this)
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory)[TopicModelView::class.java]
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topics_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayout(view)
    }

    private fun setupLayout(view: View){
        activity?.title = getString(R.string.title_selection_quiz_topics)
        recyclerView = view.findViewById(R.id.topics_view)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        topicAdapter = TopicAdapter { tag, isSelected ->
            viewModel.saveSelectedTags(tag, isSelected = isSelected)
        }
        recyclerView?.adapter = topicAdapter

        viewModel.items.observe(this, Observer<List<TopicView>> {
            topicAdapter?.submitList(it)
            Timber.d("result $topicAdapter ${it.size}")
        })

        val floatingActionButton = view.findViewById(R.id.commit_button) as FloatingActionButton
        floatingActionButton.setOnClickListener {
            navigateToQuiz()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_all -> {
                viewModel.selectAllTopics()
                true
            }
            R.id.unselect_all -> {
                viewModel.deselectAllTopics()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun navigateToQuiz() {
        activity?.run {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container_content, QuizMainFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        viewModel.loadTopics(newText)
        recyclerView?.smoothScrollToPosition(0)
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater)
        menuInflater.inflate(R.menu.activity_topic, menu)
        menuInflater.inflate(R.menu.show_total_summary, menu)

        val item = menu.findItem(R.id.action_search)
        val searchView = MenuItemCompat.getActionView(item) as SearchView
        searchView.setOnQueryTextListener(this)
    }


    companion object {
        @JvmStatic
        fun newInstance(): TopicFragment {
            return TopicFragment()
        }
    }

}
