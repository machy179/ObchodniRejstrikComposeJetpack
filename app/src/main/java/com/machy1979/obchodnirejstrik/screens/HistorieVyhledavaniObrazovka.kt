package com.machy1979.obchodnirejstrik.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.machy1979.obchodnirejstrik.model.Query
import com.machy1979.obchodnirejstrik.screens.components.ObycPolozkaHodnota
import com.machy1979.obchodnirejstrik.ui.theme.ColorBorderStroke
import com.machy1979.obchodnirejstrik.ui.theme.PaddingVnitrniCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostBorderStrokeCard
import com.machy1979.obchodnirejstrik.ui.theme.VelikostElevation
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardHorizontal
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingCardVertical
import com.machy1979.obchodnirejstrik.ui.theme.VelikostPaddingHlavnihoOkna
import com.machy1979.obchodnirejstrik.ui.theme.VelikostZakulaceniRohu
import com.machy1979.obchodnirejstrik.viewmodel.ObchodniRejstrikViewModel

@Composable
fun HistorieVyhledavaniObrazovka(
    viewModel: ObchodniRejstrikViewModel = hiltViewModel(),
    hledejDleIcoButton: (String) -> Unit = {},
    modifier: Modifier = Modifier
){
      //history list:
    val queryList = viewModel.queryList.collectAsState().value
    val nactenoQueryList by viewModel.nactenoQueryList.collectAsState()


    Column (
        modifier = modifier
            .padding(VelikostPaddingHlavnihoOkna)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ){
// Fetching current app configuration

        val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        var expanded by rememberSaveable { mutableStateOf (false) }
        val paddingModifierHlavniCard = if (isLandscape) {
            Modifier.padding(start = 20.dp, end = 20.dp, top = 2.dp, bottom = 10.dp)
        } else {
            Modifier.padding(start = 20.dp, end = 20.dp, top = 100.dp, bottom = 10.dp)
        }
        val paddingModifierSpodniCard = if (expanded) {
            Modifier
                .fillMaxWidth()
                .padding(PaddingVnitrniCard)
                .clickable {
                    expanded = !expanded
                }
        } else {
            Modifier
                .padding(0.dp)
                .clickable {
                    expanded = !expanded
                }
        }




        if(nactenoQueryList) {

                ListOfHistory(queryList,
                    goToIcoButton = {
                        hledejDleIcoButton(it)
                    })


        }




    }
}

@Composable
fun ListOfHistory(queryList: List<Query>,
                  goToIcoButton: (String) -> Unit = {}) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

            }

                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Seznam položek
                        queryList.forEach { query ->
                            Card(
                                shape = RoundedCornerShape(size = VelikostZakulaceniRohu),
                                border = BorderStroke(width = VelikostBorderStrokeCard, color = ColorBorderStroke),
                                elevation = VelikostElevation,
                                modifier = Modifier
                                    .padding(horizontal = VelikostPaddingCardHorizontal, vertical = VelikostPaddingCardVertical)
                                    .fillMaxWidth()
                                    .clickable {
                                        goToIcoButton(query.ico)
                                    }
                            ) {
                                Column {
                                    Row {
                                        Spacer(Modifier.weight(1f))
                                    }

                                    ObycPolozkaHodnota(query.name, true, true)
                                    ObycPolozkaHodnota("ICO: " + query.ico, true, false)
                                     ObycPolozkaHodnota(query.address, false, false)
                                }
                            }
                        }
                    }
                }



        }


}