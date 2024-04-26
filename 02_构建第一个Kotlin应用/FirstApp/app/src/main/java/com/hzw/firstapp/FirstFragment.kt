package com.hzw.firstapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.hzw.firstapp.databinding.FragmentFirstBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 给TOAST按钮添加点击监听事件，显示Toast
        binding.toastButton.setOnClickListener {
            val myToast = Toast.makeText(context, "Hello Toast!", Toast.LENGTH_LONG)
            // 显示Toast
            myToast.show()
        }
        // 给COUNT按钮添加点击事件
        binding.countButton.setOnClickListener {
            countMe(view);
        }

        // 给RANDOM按钮添加点击监听事件，跳转到第二个界面
        binding.randomButton.setOnClickListener {
            // 获取textview_first的值
            val currentCount = binding.textviewFirst.text.toString().toInt()
            // 将该值作为参数传递给actionFirstFragmentToSecondFragment()
            val action = FirstFragmentDirections.actionFirstFragmentToSecondFragment(currentCount)
            // 根据action跳转
            findNavController().navigate(action)
        }
    }
    // 计数函数
    private fun countMe(view: View) {
        // 获取TextView中的内容
        val countString = binding.textviewFirst.text.toString()
        // 将字符串转为int型数值
        var count = countString.toInt()
        // 计数+1
        count++
        // 将新的数值展示在TextView上
        binding.textviewFirst.text = count.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}