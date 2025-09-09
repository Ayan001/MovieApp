package com.appdemo.presentation.features.movie

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.appdemo.domain.model.Genre
import com.appdemo.presentation.contracts.movie.MovieListContract

@Composable
fun MovieTopBar(
    selectedGenreName: String?,
    genres: List<Genre>,
    onGenreClick: () -> Unit,
    onGenreSelected: (Genre?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var count=0

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                onGenreClick()
                expanded = true
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Select Genre")
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Open")
            }
        }

        Spacer(modifier = Modifier.width(12.dp))


            Text(text = selectedGenreName ?: "All"
            )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .wrapContentWidth()
        ) {
            // All item
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isAllSelected = selectedGenreName == null
                        if (isAllSelected) {
                            Icon(Icons.Default.Check, contentDescription = "selected", modifier = Modifier.size(18.dp))
                        } else {
                            Spacer(modifier = Modifier.size(18.dp))
                        }
                        count =genres.sumOf { it.count }
                        Text(text = "All (${count})", modifier = Modifier.fillMaxWidth().clickable {
                            // optional: clickable on text itself
                            val genre=Genre("All",count)
                            onGenreSelected(genre)
                            expanded = false
                        })
                    }
                },
                onClick = {
                    expanded = false
                    onGenreSelected(null)
                }
            )

            HorizontalDivider()

            // genres
            if (genres.isEmpty()) {
                DropdownMenuItem(text = { Text("Loading..") }, onClick = {})
            } else {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val selected = genre.name == selectedGenreName
                                if (selected) {
                                    Icon(Icons.Default.Check, contentDescription = "selected", modifier = Modifier.size(18.dp))
                                } else {
                                    Spacer(modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "${genre.name} (${genre.count})")
                            }
                        },
                        onClick = {
                            expanded = false
                            onGenreSelected(genre)
                        }
                    )
                }
            }
        }
    }
}