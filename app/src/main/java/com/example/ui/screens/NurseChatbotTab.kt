package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ELmorsyViewModel
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.MedicalBlue
import com.example.ui.theme.SoftBlueBg

@Composable
fun NurseChatbotTab(viewModel: ELmorsyViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsState()
    var supportMsgText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.initSupportChat()
    }
    
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(bottom = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(SoftBlueBg, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(chatMessages) { msg ->
                    val isApp = msg.id.startsWith("system")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = if (isApp) Arrangement.Start else Arrangement.End
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = if (isApp) Color.White else MedicalBlue),
                            shape = RoundedCornerShape(
                                topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = if (isApp) 0.dp else 16.dp,
                                bottomEnd = if (isApp) 16.dp else 0.dp
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.padding(horizontal = 4.dp).widthIn(max = 260.dp)
                        ) {
                            Text(
                                text = msg.content,
                                color = if (isApp) DarkNavy else Color.White,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = supportMsgText,
                onValueChange = { supportMsgText = it },
                placeholder = { Text("أكتب استفسارك...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f).fillMaxHeight(),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MedicalBlue, unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (supportMsgText.isNotBlank()) {
                        viewModel.sendChatMessage(supportMsgText, isHelpdesk = true)
                        supportMsgText = ""
                    }
                },
                modifier = Modifier.size(56.dp).background(MedicalBlue, RoundedCornerShape(14.dp))
            ) {
                Icon(Icons.Default.Send, contentDescription = "إرسال", tint = Color.White)
            }
        }
    }
}
