import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rezerwacje.ui.viewmodel.GoogleAuthViewModel

class GoogleAuthViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GoogleAuthViewModel() as T
    }
}