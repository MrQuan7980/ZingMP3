import android.content.Context
import android.view.View
import android.widget.Toast

fun Context.setMessage(message: String)
{
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun checkEmail(text : String) : Boolean
{
    if (text.isEmpty())
    {
        return false
    }
    return true
}

fun progressbar(boolean: Boolean, progressbar: View, button : View, viewProgress : Int, buttonView: Int)
{
    if(boolean)
    {
        progressbar.visibility = viewProgress
        button.visibility = buttonView
    }
    else
    {
        progressbar.visibility = buttonView
        button.visibility = viewProgress
    }
}

fun showView(view : View, state : Int)
{
    view.visibility = state
}


fun returnOTP(otp1 : String, otp2 : String, otp3 : String, otp4 : String, otp5 : String, otp6 : String) : String{
    return listOf(otp1, otp2, otp3, otp4, otp5, otp6).joinToString("") { it.trim() }
}