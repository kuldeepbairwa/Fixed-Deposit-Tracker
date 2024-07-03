package dev.abhaycloud.fdtracker.presentation.ui.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.abhaycloud.fdtracker.R
import dev.abhaycloud.fdtracker.presentation.ui.components.FixedDepositField
import dev.abhaycloud.fdtracker.utils.Utils.toIndianFormat
import kotlin.math.round

@Composable
fun CalculatorScreen(viewModel: CalculatorViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val isSliderTabSelected by viewModel.isSliderTabSelected.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "FD Calculator", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Image(
                painter = painterResource(id = if (!isSliderTabSelected) R.drawable.outline_sliders_24 else R.drawable.outline_keyboard_24),
                contentDescription = "slider",
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground),
                modifier = Modifier.clickable {
                    viewModel.onTabChange(isSliderTabSelected = !isSliderTabSelected)
                })
        }
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedVisibility(isSliderTabSelected) {
            FixedDepositCalculatorWithSlider(uiState = uiState, viewModel = viewModel)
        }
        AnimatedVisibility(!isSliderTabSelected) {
            FixedDepositCalculatorWithFields(uiState = uiState, viewModel = viewModel)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Estimated maturity amount: ₹${
                uiState.maturityAmount.toIndianFormat(
                    includeDecimal = true
                )
            }",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FixedDepositCalculatorWithSlider(uiState: CalculatorUIState, viewModel: CalculatorViewModel) {
    Column {
        FixedDepositSliderItem(
            title = "Principal Amount",
            sliderTitle = "₹${uiState.principalAmount.toIndianFormat()}",
            sliderValue = uiState.principalAmount.toFloat(),
            valueRange = 1f..1000000f,
//            steps = 99999
        ) {
            viewModel.onPrincipalAmountChange(it.toInt())
        }
        Spacer(modifier = Modifier.height(16.dp))
        FixedDepositSliderItem(
            title = "Annual Interest Rate",
            sliderTitle = "%.2f".format(uiState.annualInterestRate),
            sliderValue = uiState.annualInterestRate.toFloat(),
            valueRange = 1f..15f,
            steps = 14
        ) {
            viewModel.onAnnualInterestChange(it.toDouble())
        }
        Spacer(modifier = Modifier.height(16.dp))
        FixedDepositSliderItem(
            title = "Investment Duration",
            sliderTitle = "${uiState.period} years",
            sliderValue = uiState.period.toFloat(),
            valueRange = 1f..25f,
            steps = 24
        ) {
            viewModel.onYearChange(it.toInt())
        }
    }
}

@Composable
fun FixedDepositCalculatorWithFields(uiState: CalculatorUIState, viewModel: CalculatorViewModel) {
    Column {
        FixedDepositField(
            title = "Principal Amount",
            value = uiState.principalAmount.toIndianFormat(),
            isNumericField = true,
            isDecimalAllowed = false
        ) {
            viewModel.onPrincipalAmountChange(if (it.isEmpty()) 1 else it.toInt())
        }
        Spacer(modifier = Modifier.height(16.dp))
        FixedDepositField(
            title = "Annual Interest Rate",
            value = "%.2f".format(uiState.annualInterestRate),
            isNumericField = true
        ) {
            viewModel.onAnnualInterestChange(if (it.isEmpty()) 1.0 else it.toDouble())
        }
        Spacer(modifier = Modifier.height(16.dp))
        FixedDepositField(
            title = "Investment Duration",
            value = uiState.period.toString(),
            isNumericField = true,
            isDecimalAllowed = false,
            isLastField = true
        ) {
            viewModel.onYearChange(if (it.isEmpty()) 1 else it.toInt())
        }
    }
}

@Composable
fun FixedDepositSliderItem(
    title: String,
    sliderTitle: String,
    sliderValue: Float,
    steps: Int = 0,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.fillMaxWidth(0.29f),
                text = sliderTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Slider(modifier = Modifier
                .weight(1f),
                value = sliderValue,
                valueRange = valueRange,
                steps = steps,
                onValueChange = {
                    onValueChange.invoke(round(it))
                })
        }
    }
}

@Preview
@Composable
fun CalculatorScreenPreview() {
    CalculatorScreen()
}
