package com.example.languagelearning

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Data Classes
data class Lesson(val title: String, val content: String)
data class QuizQuestion(val question: String, val options: List<String>, val correctAnswer: String)
data class UserProfile(val username: String, val learnedLessons: List<Lesson>)
data class CommunityPost(val username: String, val title: String, val content: String)

// ViewModel
class LanguageViewModel : ViewModel() {
    var lessons by mutableStateOf(mutableListOf<Lesson>())
    var quizzes by mutableStateOf(mutableListOf<QuizQuestion>())
    var userProfiles by mutableStateOf(mutableListOf<UserProfile>())
    var communityPosts by mutableStateOf(mutableListOf<CommunityPost>())
    var currentLesson by mutableStateOf<Lesson?>(null)
    var currentQuiz by mutableStateOf<QuizQuestion?>(null)
    var quizResults by mutableStateOf(mutableMapOf<Int, Boolean>())
    var score by mutableStateOf(0)

    fun addLesson(title: String, content: String) {
        lessons.add(Lesson(title, content))
    }

    fun addQuiz(question: String, options: List<String>, correctAnswer: String) {
        quizzes.add(QuizQuestion(question, options, correctAnswer))
    }

    fun addUserProfile(username: String) {
        userProfiles.add(UserProfile(username, mutableListOf()))
    }

    fun addCommunityPost(username: String, title: String, content: String) {
        communityPosts.add(CommunityPost(username, title, content))
    }

    fun submitAnswer(quizIndex: Int, answer: String) {
        val correct = quizzes[quizIndex].correctAnswer == answer
        quizResults[quizIndex] = correct
        updateScore()
    }

    private fun updateScore() {
        val totalQuestions = quizzes.size
        val correctAnswers = quizResults.values.count { it }
        score = if (totalQuestions > 0) correctAnswers * 100 / totalQuestions else 0
    }
}

// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LanguageApp()
            }
        }
    }
}

// Composable Functions
@Composable
fun HomeScreen(navController: NavController, viewModel: LanguageViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Language Learning App", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("lessons") }) {
            Text("Lessons")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("quizzes") }) {
            Text("Quizzes")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("community") }) {
            Text("Community")
        }
    }
}

@Composable
fun LessonsScreen(navController: NavController, viewModel: LanguageViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lessons", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("create_lesson") }) {
            Text("Create New Lesson")
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.lessons.forEachIndexed { index, lesson ->
            Button(onClick = { navController.navigate("lesson_detail/$index") }) {
                Text(lesson.title)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun LessonDetailScreen(navController: NavController, viewModel: LanguageViewModel, lessonIndex: Int) {
    val lesson = viewModel.lessons[lessonIndex]
    viewModel.currentLesson = lesson

    Column(modifier = Modifier.padding(16.dp)) {
        Text(lesson.title, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Text(lesson.content)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun CreateLessonScreen(navController: NavController, viewModel: LanguageViewModel) {
    var lessonTitle by remember { mutableStateOf("") }
    var lessonContent by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Create New Lesson", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = lessonTitle,
            onValueChange = { lessonTitle = it },
            label = { Text("Lesson Title") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = lessonContent,
            onValueChange = { lessonContent = it },
            label = { Text("Lesson Content") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (lessonTitle.isNotEmpty() && lessonContent.isNotEmpty()) {
                viewModel.addLesson(lessonTitle, lessonContent)
                navController.popBackStack()
            }
        }) {
            Text("Create Lesson")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun QuizzesScreen(navController: NavController, viewModel: LanguageViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Quizzes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("create_quiz") }) {
            Text("Create New Quiz")
        }
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.quizzes.forEachIndexed { index, quiz ->
            Button(onClick = { navController.navigate("quiz_detail/$index") }) {
                Text(quiz.question)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun QuizDetailScreen(navController: NavController, viewModel: LanguageViewModel, quizIndex: Int) {
    val quiz = viewModel.quizzes[quizIndex]
    viewModel.currentQuiz = quiz

    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(quiz.question, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        quiz.options.forEachIndexed { index, option ->
            Button(
                onClick = {
                    selectedAnswer = option
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedAnswer == option) Color.LightGray else Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("${index + 1}. $option", color = Color.Black)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                selectedAnswer?.let {
                    viewModel.submitAnswer(quizIndex, it)
                    navController.navigate("quiz_results")
                }
            },
            enabled = selectedAnswer != null,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Answer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun CreateQuizScreen(navController: NavController, viewModel: LanguageViewModel) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf("") }
    var correctAnswer by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Create New Quiz", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Question") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = options,
            onValueChange = { options = it },
            label = { Text("Options (comma separated)") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = correctAnswer,
            onValueChange = { correctAnswer = it },
            label = { Text("Correct Answer") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val optionsList = options.split(",").map { it.trim() }
            if (question.isNotEmpty() && optionsList.isNotEmpty() && correctAnswer.isNotEmpty()) {
                viewModel.addQuiz(question, optionsList, correctAnswer)
                navController.popBackStack()
            }
        }) {
            Text("Create Quiz")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun QuizResultsScreen(navController: NavController, viewModel: LanguageViewModel) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Quiz Results", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        viewModel.quizResults.forEach { (index, isCorrect) ->
            Text("Quiz ${index + 1}: ${if (isCorrect) "Correct" else "Incorrect"}")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Your score: ${viewModel.score}%", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun CommunityScreen(navController: NavController, viewModel: LanguageViewModel) {
    var username by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Community", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Input fields for creating a new community post
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            maxLines = 3
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (username.isNotEmpty() && title.isNotEmpty() && content.isNotEmpty()) {
                viewModel.addCommunityPost(username, title, content)
                username = ""
                title = ""
                content = ""
            }
        }) {
            Text("Post")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Display community posts
        viewModel.communityPosts.forEachIndexed { index, post ->
            CommunityPostCard(post)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.popBackStack() }) {
            Text("Go Back")
        }
    }
}

@Composable
fun CommunityPostCard(post: CommunityPost) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.username, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            Text(post.title, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.content)
        }
    }
}

@Composable
fun LanguageApp() {
    val navController = rememberNavController()
    val viewModel: LanguageViewModel = viewModel()

    NavHost(navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, viewModel) }
        composable("lessons") { LessonsScreen(navController, viewModel) }
        composable("create_lesson") { CreateLessonScreen(navController, viewModel) }
        composable("lesson_detail/{lessonIndex}") { backStackEntry ->
            val lessonIndex = backStackEntry.arguments?.getString("lessonIndex")?.toIntOrNull() ?: 0
            LessonDetailScreen(navController, viewModel, lessonIndex)
        }
        composable("quizzes") { QuizzesScreen(navController, viewModel) }
        composable("create_quiz") { CreateQuizScreen(navController, viewModel) }
        composable("quiz_detail/{quizIndex}") { backStackEntry ->
            val quizIndex = backStackEntry.arguments?.getString("quizIndex")?.toIntOrNull() ?: 0
            QuizDetailScreen(navController, viewModel, quizIndex)
        }
        composable("quiz_results") { QuizResultsScreen(navController, viewModel) }
        composable("community") { CommunityScreen(navController, viewModel) }
    }
}
