package net.itanchi.addeep.android.ui.screen.auth.login.countries

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.itanchi.addeep.android.R
import net.itanchi.addeep.android.data.Countries
import net.itanchi.addeep.android.data.Country
import net.itanchi.addeep.android.util.FlowAsState
import net.itanchi.addeep.android.ui.screen.common.SearchBar
import net.itanchi.addeep.android.util.ViewState
import org.koin.androidx.compose.getViewModel

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CountriesScreen(
    viewModel: CountriesViewModel = getViewModel(),
) {
    val countriesViewState = FlowAsState(
        LocalLifecycleOwner.current,
        viewModel.countriesViewState,
        viewModel.countriesViewState.value,
    )
    CountriesScreenContent(
        viewModel::handleEvent,
        countriesViewState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreenContent(
    event: (CountriesEvent) -> Unit,
    countriesViewState: ViewState,
) {
    var searchText by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                SmallTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { event(CountriesEvent.Close) }) {
                            Icon(painterResource(R.drawable.ic_close_line), contentDescription = null)
                        }
                    },
                    title = { Text(stringResource(R.string.countries_title)) },
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    searchText = searchText,
                    hint = stringResource(R.string.countries_search),
                    onSearch = { searchText = it }
                )
                CountryList(
                    Countries.filter {
                        with(searchText.trim()) {
                            it.name.contains(this, true)
                                    || it.nameCode.contains(this, true)
                                    || it.phoneCode.contains(this, true)
                        }
                    },
                    onItemClick = {
                        event(CountriesEvent.SelectCountry(it.nameCode))
                    }
                )
            }
        }
    }
}

@Composable
private fun CountryList(
    countries: List<Country>,
    onItemClick: (Country) -> Unit,
) {
    LazyColumn {
        items(countries) { country ->
            CountryRow(Modifier.padding(horizontal = 24.dp), country) { onItemClick(it) }
            Divider(Modifier.padding(horizontal = 24.dp))
        }
    }
}

@Composable
private fun CountryRow(
    modifier: Modifier,
    country: Country,
    onClick: (Country) -> Unit,
) {
    Text(
        country.name,
        modifier = modifier.fillMaxWidth().clickable { onClick(country) }.padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}