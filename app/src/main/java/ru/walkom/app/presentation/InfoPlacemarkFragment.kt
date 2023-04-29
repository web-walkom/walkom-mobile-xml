package ru.walkom.app.presentation

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import ru.walkom.app.R
import ru.walkom.app.databinding.FragmentInfoPlacemarkBinding
import ru.walkom.app.domain.model.SliderModel
import java.lang.Math.abs

const val ARG_ITEM_COUNT = "item_count"

class ItemListDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentInfoPlacemarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var sliderImages: ArrayList<SliderModel>
    private lateinit var slider: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoPlacemarkBinding.inflate(inflater, container, false)

        slider = binding.slider

        sliderImages = ArrayList<SliderModel>()

        sliderImages.add(
            SliderModel(
                0, "https://i2.photo.2gis.com/images/geo/16/2251799857021769_94b1.jpg"
            )
        )
        sliderImages.add(
            SliderModel(
                1, "https://webpulse.imgsmail.ru/imgpreview?mb=webpulse&key=pulse_cabinet-image-2f1f4bad-a8b1-490c-ae47-6b5d3c74da70"
            )
        )
        sliderImages.add(
            SliderModel(
                2, "https://i2.photo.2gis.com/images/geo/0/30258560051657129_873c.jpg"
            )
        )

        val adapter = activity?.let { ItemAdapter(sliderImages, it) }

        adapter?.notifyDataSetChanged()
        slider.adapter = adapter
        slider.clipChildren = false
        slider.clipToPadding = false
        slider.offscreenPageLimit = 3
        slider.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(40))
        compositePageTransformer.addTransformer(object: ViewPager2.PageTransformer {
            override fun transformPage(page: View, position: Float) {
                val r = 1 - abs(position)
                page.scaleY = 0.85f + r * 0.15f
            }
        })

        slider.setPageTransformer(compositePageTransformer)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    private inner class ViewHolder internal constructor(binding: View) :
        RecyclerView.ViewHolder(binding) {
    }

    private class ItemAdapter constructor(private val list: ArrayList<SliderModel>, var context: Context): RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            val sliderImage: ImageView = itemView.findViewById(R.id.sliderImage)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val currentItem = list[position]
            Glide.with(context.applicationContext)
                .load(currentItem.image)
                .into(holder.sliderImage)

            holder.itemView.setOnClickListener {
                Toast.makeText(context, currentItem.id, Toast.LENGTH_SHORT).show()
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    companion object {
        fun newInstance(itemCount: Int): ItemListDialogFragment =
            ItemListDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}