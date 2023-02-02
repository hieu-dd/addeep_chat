package net.itanchi.addeep.android.ui.screen.auth.login.countries

sealed class CountriesEvent {
    object Close : CountriesEvent()
    data class SelectCountry(
        val countryCode: String,
    ) : CountriesEvent()
}