package edu.uw.ischool.opensecrets

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import edu.uw.ischool.opensecrets.databinding.SearchBinding


const val SORTBY : String = "SortBy"
const val ASCEND : String = "Ascending"
const val FILTERTEXT : String = "FilterText"
const val FILTERBY : String = "FilterBy"
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: SearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.addButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    EntryTextActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        binding.homeButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    HomeActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
        binding.optionButton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    OptionActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }


        ArrayAdapter.createFromResource(
            this,
            R.array.sort_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.sortSpinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.ascend_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.ascendSpinner.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.filter_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.filterSpinner.adapter = adapter
        }
        binding.startSearchButton.setOnClickListener {
            val homeIntent : Intent = Intent(
                this,
                HomeActivity::class.java
            ).apply {
                putExtra(SORTBY, binding.sortSpinner.selectedItem as String)
                putExtra(ASCEND, binding.ascendSpinner.selectedItem as String == "Ascend")
                val filterText : String = binding.filterEditText.text.toString()
                Log.i("SearchActivity", "FilterText: $filterText")
                if(filterText.isNotEmpty()){
                    putExtra(FILTERTEXT, filterText)
                    putExtra(FILTERBY, binding.filterSpinner.selectedItem as String)
                }
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            }

            startActivity(homeIntent)
        }

    }
}