package edu.sfsu.csc680.gatoronthego

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import edu.sfsu.csc680.gatoronthego.databinding.FragmentInfoBinding

class InfoFragment : Fragment() {

    private lateinit var binding : FragmentInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_info, container, false
        )

        binding.infoEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, ADDRESS)
                putExtra(Intent.EXTRA_SUBJECT, SUBJECT)
            }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            }
        }



        return binding.root
    }

    companion object {
        const val ADDRESS = "tyin2@mail.sfsu.edu"
        const val SUBJECT = "Feedbacks on Gator On The Go"
    }
}