//package com.example.ui
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//
//@Composable
//fun PantallaLogin() {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = Color.White
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(20.dp)
//        ) {
//            // Título
//            Text(
//                text = "Pantalla Login a Android",
//                fontSize = 32.sp,
//                fontWeight = FontWeight.ExtraBold,
//                color = Color(0xFF111827) // gris muy oscuro
//            )
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            // Fila: "Open" + metadata
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                StatusChipOpen()
//
//                Spacer(modifier = Modifier.width(10.dp))
//
//                Text(
//                    text = buildAnnotatedString {
//                        append("Issue created 1 day ago by ")
//                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
//                            append("Javier Sánchez Osuna")
//                        }
//                    },
//                    fontSize = 14.sp,
//                    color = Color(0xFF6B7280) // gris medio
//                )
//            }
//
//            Spacer(modifier = Modifier.height(18.dp))
//
//            // Descripción
//            Text(
//                text = "Crear la pantalla de Login amb Jetpack Compose seguint MVVM i StateFlow. " +
//                        "Inclou formulari, validacions bàsiques i gestió d’estats (loading, error, èxit).",
//                fontSize = 16.sp,
//                color = Color(0xFF374151), // gris oscuro
//                lineHeight = 22.sp
//            )
//        }
//    }
//}
//
//@Composable
//private fun StatusChipOpen() {
//    val green = Color(0xFF16A34A)
//
//    Row(
//        modifier = Modifier
//            .clip(RoundedCornerShape(999.dp))
//            .background(Color(0xFFEAF7EF))
//            .padding(horizontal = 10.dp, vertical = 6.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Box(
//            modifier = Modifier
//                .size(10.dp)
//                .clip(CircleShape)
//                .background(green)
//        )
//        Spacer(modifier = Modifier.width(6.dp))
//        Text(
//            text = "Open",
//            fontSize = 14.sp,
//            fontWeight = FontWeight.SemiBold,
//            color = green
//        )
//    }
//}
