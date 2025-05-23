package com.noveleta.sabongbetting.widgets;

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

object BetInfoCards {

    /**
     * A customizable Material3 info card showing a title, payout, and total bets.
     *
     * @param title The bold title displayed at the top and centered.
     * @param payout The payout amount, shown next to a bold "Payout:" label.
     * @param totalBets The total bets amount, shown next to a bold "Total Bets:" label.
     * @param backgroundColor The background color of the card.
     * @param modifier Optional [Modifier] for styling and layout.
     */
    @Composable
fun InfoCard(
    title: String,
    payout: String,
    totalBets: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Payout row
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Payout:",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = payout,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Total Bets row
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Total Bets:",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
                Text(
                    text = totalBets,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}


}
