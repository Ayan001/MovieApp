package com.appdemo.coreui.component
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension


@Composable
fun MovieItem(
    modifier: Modifier,
    title: String?,
    year: String?,
    overview: String?,
    genre: String?
){
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(2.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ){
        ConstraintLayout(
            modifier = modifier.fillMaxWidth()
        ) {
            val (titleRef, lineRef, overviewRef, genreRef) = createRefs()
            Row (modifier = Modifier
                .height(25.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(3.dp)
                .constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }, verticalAlignment = Alignment.CenterVertically){
                //Title
                if (title != null) {
                    Text(modifier = Modifier
                        .weight(1f),
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        )
                }
                //Year
                Text(
                    text = "($year)",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                )

            }

            Row (
                modifier=Modifier.constrainAs(lineRef){
                    top.linkTo(titleRef.bottom)
                }
            ){
                HorizontalDivider()
            }

            //Overview
            if (overview != null) {
                Text(
                    text = overview,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.constrainAs(overviewRef) {
                        top.linkTo(titleRef.bottom, margin = 3.dp)
                        start.linkTo(parent.start, margin = 3.dp)
                        end.linkTo(parent.end, margin = 3.dp)
                        width = Dimension.fillToConstraints
                    }.padding(3.dp)
                )
            }

            //Genre
            if (genre != null) {
                Text(
                    text = genre,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.DarkGray),
                    modifier = Modifier.constrainAs(genreRef) {
                        top.linkTo(overviewRef.bottom, margin = 8.dp)
                        end.linkTo(parent.end, margin = 8.dp)
                        bottom.linkTo(parent.bottom,margin = 8.dp)
                    }
                )
            }
        }

    }
}


@Preview
@Composable
fun MovieListItemPreview() {
    Surface {
        MovieItem(
            modifier = Modifier,
            title = "AGSJNDSJJ",
            year = "2021",
            overview = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged",
            genre = "dhurte"
        )
    }
}
