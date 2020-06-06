import com.backbase.assignment.ui.model.Results

data class NowPlaying (

	val results : List<Results>,
	val page : Int,
	val total_results : Int,
	val dates : Dates,
	val total_pages : Int
)