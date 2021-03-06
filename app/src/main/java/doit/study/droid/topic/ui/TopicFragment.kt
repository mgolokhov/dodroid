package doit.study.droid.topic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import doit.study.droid.R
import doit.study.droid.app.App
import doit.study.droid.databinding.FragmentTopicBinding
import javax.inject.Inject

class TopicFragment : Fragment(), SearchView.OnQueryTextListener {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
//    private lateinit var viewModel: TopicViewModel
    private val viewModel by viewModels<TopicViewModel> { viewModelFactory }
    private lateinit var viewDataBinding: FragmentTopicBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        App.dagger.inject(this)
        super.onCreate(savedInstanceState)

//        viewModel = ViewModelProviders.of(this, viewModelFactory)[TopicViewModel::class.java]
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewDataBinding = FragmentTopicBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }

    private fun setupLayout() {
        activity?.title = getString(R.string.title_selection_quiz_topics)

        viewDataBinding.apply {
            lifecycleOwner = viewLifecycleOwner
            topicsList.adapter = TopicAdapter(viewModel)
        }

        viewDataBinding.commitFabButton.setOnClickListener {
            navigateToQuiz()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupLayout()
        viewModel.loadTopics()
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
        findNavController().navigate(R.id.action_topic_fragment_dest_to_quizMainFragment, null)
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String): Boolean {
        viewModel.loadTopics(newText)
        viewDataBinding.topicsList.smoothScrollToPosition(0)
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
