# Step-by-Step Guide: Building a Pension Fund Benefit Simulation with Kotlin Coroutines

## 📋 Table of Contents
1. [Case Study Overview](#case-study-overview)
2. [Problem Analysis](#problem-analysis)
3. [Solution Architecture](#solution-architecture)
4. [Implementation Steps](#implementation-steps)
5. [Understanding Coroutines](#understanding-coroutines)
6. [Testing the Application](#testing-the-application)
7. [Key Takeaways](#key-takeaways)

---

## 🎯 Case Study Overview

### Business Context: Pension Fund (BPJS Ketenagakerjaan-like)

**The Problem:**
Users of a pension fund system often ask: *"If I retire today, how much benefit will I receive?"*

This is **NOT** creating a pension claim, just a **benefit simulation**.

### What We Need to Calculate:

To answer this question, the system must fetch data from multiple sources:

1. **Participant Profile** - Who is the person? (name, age, registration date)
2. **Contribution History** - How much have they contributed over the years?
3. **Current Fund Return Rate** - What is the investment return rate?
4. **Pension Rules** - What are the retirement age and benefit formulas?
5. **Calculate Total Benefit** - Use all the above data to compute the benefit

### The Challenge:

This operation is:
- ✅ **Read-heavy** - Only reading data, not modifying anything
- ✅ **IO-heavy** - Multiple database/service calls
- ✅ **Time-sensitive** - Users want fast responses
- ✅ **Perfect for parallel execution** - Data sources are independent

---

## 🔍 Problem Analysis

### Sequential vs Parallel Execution

#### ❌ **Sequential Approach (Slow)**
```
Fetch Participant Profile  → 150ms
Fetch Contribution History  → 300ms
Fetch Fund Return Rate      → 100ms
Fetch Pension Rules         → 80ms
─────────────────────────────────────
Total Time:                   630ms
```

#### ✅ **Parallel Approach with Coroutines (Fast)**
```
Fetch Participant Profile  → 150ms
├─ Fetch Contribution History  → 300ms (parallel)
├─ Fetch Fund Return Rate      → 100ms (parallel)
└─ Fetch Pension Rules         → 80ms  (parallel)
─────────────────────────────────────
Total Time:                   450ms (150ms + max(300ms))
```

**Result:** ~40% faster with coroutines!

---

## 🏗️ Solution Architecture

### Project Structure

```
src/main/kotlin/net/research/kt/coroutine/
├── CoroutineSampleApplication.kt         # Main entry point
├── config/
│   └── CoroutineSampleConfiguration.kt   # Configuration classes
├── domain/
│   ├── ParticipantProfile.kt             # User data model
│   ├── ContributionHistory.kt            # Contribution data model
│   ├── FundReturnRate.kt                 # Investment return data model
│   ├── PensionRules.kt                   # Business rules model
│   └── BenefitSimulationResult.kt        # Result model
├── repository/
│   ├── ParticipantRepository.kt          # Data access for participants
│   ├── ContributionRepository.kt         # Data access for contributions
│   ├── FundReturnRateRepository.kt       # Data access for fund rates
│   └── PensionRulesRepository.kt         # Data access for rules
├── service/
│   └── BenefitSimulationService.kt       # ⭐ Core business logic with coroutines
├── api/
│   ├── BenefitSimulationResource.kt      # REST API endpoints
│   └── HealthCheckResource.kt            # Health check endpoint
├── exception/
│   └── Exceptions.kt                     # Custom exceptions
└── health/
    └── CoroutineHealthCheck.kt           # Health check implementation
```

### Architecture Layers

```
┌─────────────────────────────────────┐
│     REST API Layer (Resources)      │  ← User requests come here
├─────────────────────────────────────┤
│     Service Layer (Business Logic)  │  ← ⭐ Coroutines work here
├─────────────────────────────────────┤
│     Repository Layer (Data Access)  │  ← Database/external calls
├─────────────────────────────────────┤
│     Domain Layer (Data Models)      │  ← Data structures
└─────────────────────────────────────┘
```

---

## 🛠️ Implementation Steps

### Step 1: Define Domain Models

**Location:** `src/main/kotlin/net/research/kt/coroutine/domain/`

#### 1.1 Participant Profile (`ParticipantProfile.kt`)

This represents the user/participant in the pension system.

```kotlin
data class ParticipantProfile(
    val participantId: String,      // Unique ID
    val name: String,                // Full name
    val birthDate: LocalDate,        // Date of birth
    val registrationDate: LocalDate, // When they joined
    val employerName: String,        // Company name
    val currentSalary: Double        // Current salary
) {
    fun getAge(): Int {
        // Calculate age from birthDate
    }

    fun getYearsOfParticipation(): Int {
        // Calculate years since registration
    }
}
```

**Why?** We need to know who the person is and their basic info.

#### 1.2 Contribution History (`ContributionHistory.kt`)

This represents all monthly contributions made over the years.

```kotlin
data class ContributionHistory(
    val participantId: String,
    val contributions: List<Contribution>
) {
    fun getTotalContributions(): Double {
        return contributions.sumOf {
            it.employeeContribution + it.employerContribution
        }
    }
}

data class Contribution(
    val month: LocalDate,
    val employeeContribution: Double,  // 2% of salary
    val employerContribution: Double,  // 3.54% of salary
    val salaryBase: Double
)
```

**Why?** We need to know how much money has been contributed to calculate the benefit.

#### 1.3 Fund Return Rate (`FundReturnRate.kt`)

This represents the investment return rate.

```kotlin
data class CurrentFundReturnRate(
    val currentYear: Int,
    val currentRate: Double,
    val averageRate5Years: Double,   // Used for calculation
    val averageRate10Years: Double,  // Used for calculation
    val historicalRates: List<FundReturnRate>
)
```

**Why?** The money grows over time with investment returns. We need this rate.

#### 1.4 Pension Rules (`PensionRules.kt`)

This represents the business rules for retirement.

```kotlin
data class PensionRules(
    val normalRetirementAge: Int = 58,
    val earlyRetirementAge: Int = 50,
    val minimumYearsOfService: Int = 5,
    val earlyRetirementPenaltyRate: Double = 0.05,
    val monthlyBenefitDivisor: Int = 180
) {
    fun isEligibleForRetirement(age: Int, yearsOfService: Int): Boolean {
        return age >= earlyRetirementAge && yearsOfService >= minimumYearsOfService
    }
}
```

**Why?** We need to follow business rules to determine eligibility and penalties.

#### 1.5 Benefit Simulation Result (`BenefitSimulationResult.kt`)

This is what we return to the user.

```kotlin
data class BenefitSimulationResult(
    val participantId: String,
    val participantName: String,
    val currentAge: Int,
    val yearsOfService: Int,
    val totalContributions: Double,
    val projectedFundValue: Double,
    val estimatedLumpSum: Double,        // One-time payment
    val estimatedMonthlyBenefit: Double, // Monthly payment
    val isEligibleForRetirement: Boolean,
    val earlyRetirementPenalty: Double,
    val calculationDurationMs: Long      // How long it took
)
```

**Why?** This is the final answer we give to the user.

---

### Step 2: Create Repository Layer

**Location:** `src/main/kotlin/net/research/kt/coroutine/repository/`

Repositories handle data access. They simulate database calls with delays.

#### 2.1 Participant Repository (`ParticipantRepository.kt`)

```kotlin
class ParticipantRepository {

    suspend fun findById(participantId: String): ParticipantProfile? {
        logger.info("Fetching participant profile for: $participantId")

        // Simulate database call delay (150ms)
        delay(150)

        return participants[participantId]
    }
}
```

**Key Points:**
- `suspend` keyword = This function can be paused and resumed
- `delay(150)` = Simulates a 150ms database query
- Returns `ParticipantProfile?` = Can return null if not found

#### 2.2 Contribution Repository (`ContributionRepository.kt`)

```kotlin
class ContributionRepository {

    suspend fun findByParticipantId(participantId: String): ContributionHistory? {
        logger.info("Fetching contribution history for: $participantId")

        // Simulate database call delay (300ms - larger dataset)
        delay(300)

        return generateContributionHistory(participantId)
    }
}
```

**Why 300ms?** Contribution history is larger data (years of records), so it takes longer.

#### 2.3 Other Repositories

- `FundReturnRateRepository.kt` - Fetches investment rates (100ms delay)
- `PensionRulesRepository.kt` - Fetches business rules (80ms delay)

---

### Step 3: Implement Service Layer with Coroutines ⭐

**Location:** `src/main/kotlin/net/research/kt/coroutine/service/BenefitSimulationService.kt`

This is **THE MOST IMPORTANT CLASS** where coroutines magic happens!

#### 3.1 Service Structure

```kotlin
class BenefitSimulationService(
    private val participantRepository: ParticipantRepository,
    private val contributionRepository: ContributionRepository,
    private val fundReturnRateRepository: FundReturnRateRepository,
    private val pensionRulesRepository: PensionRulesRepository,
    private val timeoutMillis: Long = 30000
) {
    // ... methods here
}
```

#### 3.2 Main Simulation Method

```kotlin
suspend fun simulateBenefit(participantId: String): BenefitSimulationResult {
    logger.info("Starting benefit simulation for participant: $participantId")

    return withTimeout(timeoutMillis) {
        // Step 1: Fetch participant first (validates existence)
        val profile = participantRepository.findById(participantId)
            ?: throw ParticipantNotFoundException("Participant not found: $participantId")

        // Step 2: Fetch remaining data IN PARALLEL using async
        val (contributions, fundRate, rules) = coroutineScope {
            logger.info("Fetching data in parallel for participant: $participantId")

            // Create three async coroutines (run in parallel)
            val contributionsDeferred = async {
                contributionRepository.findByParticipantId(participantId)
            }

            val fundRateDeferred = async {
                fundReturnRateRepository.getCurrentReturnRate()
            }

            val rulesDeferred = async {
                pensionRulesRepository.getCurrentRules()
            }

            // Await all results (this is where parallelism happens!)
            Triple(
                contributionsDeferred.await(),
                fundRateDeferred.await(),
                rulesDeferred.await()
            )
        }

        // Step 3: Calculate benefit with fetched data
        calculateBenefit(profile, contributions, fundRate, rules)
    }
}
```

#### 3.3 Understanding the Coroutine Flow

**Step-by-Step Breakdown:**

1. **`suspend fun`** - Marks function as suspendable
   ```kotlin
   suspend fun simulateBenefit(participantId: String)
   ```

2. **`withTimeout(timeoutMillis)`** - Ensures operation doesn't take too long
   ```kotlin
   withTimeout(timeoutMillis) { /* code */ }
   ```
   If timeout exceeds 30 seconds, throws `TimeoutException`

3. **`participantRepository.findById(participantId)`** - Fetch participant FIRST
   ```kotlin
   val profile = participantRepository.findById(participantId)
       ?: throw ParticipantNotFoundException(...)
   ```
   Why first? To validate participant exists before fetching other data.

4. **`coroutineScope { }`** - Creates a scope for structured concurrency
   ```kotlin
   coroutineScope {
       // All coroutines inside must complete before continuing
   }
   ```

5. **`async { }`** - Launches a coroutine that returns a result
   ```kotlin
   val contributionsDeferred = async {
       contributionRepository.findByParticipantId(participantId)
   }
   ```
   - Returns a `Deferred<T>` (like a Future/Promise)
   - Starts running immediately
   - Doesn't block the thread

6. **`await()`** - Waits for the async result
   ```kotlin
   contributionsDeferred.await()
   ```
   - Suspends until result is ready
   - Returns the actual value

**Timeline Visualization:**

```
Time: 0ms
├─ Fetch Participant Profile starts (suspend function)
│
Time: 150ms
├─ Participant Profile received ✓
├─ Launch 3 async coroutines IN PARALLEL:
│  ├─ async { fetch contributions }  → starts
│  ├─ async { fetch fund rate }      → starts
│  └─ async { fetch pension rules }  → starts
│
Time: 230ms (150 + 80)
│  └─ Pension Rules received ✓
│
Time: 250ms (150 + 100)
│  └─ Fund Rate received ✓
│
Time: 450ms (150 + 300)
│  └─ Contributions received ✓
│  └─ All await() calls complete
│
Time: 450ms
└─ Calculate benefit and return result
```

**Total Time: ~450ms** (instead of 630ms sequential)

---

### Step 4: Create REST API Layer

**Location:** `src/main/kotlin/net/research/kt/coroutine/api/BenefitSimulationResource.kt`

This exposes HTTP endpoints for clients.

```kotlin
@Path("/api/simulations")
@Produces(MediaType.APPLICATION_JSON)
class BenefitSimulationResource(
    private val simulationService: BenefitSimulationService
) {

    @GET
    @Path("/{participantId}")
    fun simulateBenefit(
        @PathParam("participantId") participantId: String
    ): Response = runBlocking {
        // runBlocking bridges non-suspend world (JAX-RS) with suspend world (coroutines)

        return@runBlocking try {
            val result = simulationService.simulateBenefit(participantId)
            Response.ok(result).build()
        } catch (e: ParticipantNotFoundException) {
            logger.warn("Participant not found: $participantId")
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("error" to e.message))
                .build()
        } catch (e: Exception) {
            logger.error("Error simulating benefit for participant: $participantId", e)
            Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(mapOf("error" to "Internal server error"))
                .build()
        }
    }
}
```

#### Understanding `runBlocking`

**Why `runBlocking`?**

- JAX-RS (REST framework) doesn't understand `suspend` functions
- `runBlocking` creates a coroutine scope and blocks the thread until coroutines complete
- It's a bridge between blocking (traditional) and non-blocking (coroutines) code

**Flow:**
```
HTTP Request
    ↓
JAX-RS (blocking world)
    ↓
runBlocking { }  ← Bridge
    ↓
suspend function (coroutine world)
    ↓
Result
    ↓
HTTP Response
```

---

### Step 5: Wire Everything Together

**Location:** `src/main/kotlin/net/research/kt/coroutine/CoroutineSampleApplication.kt`

```kotlin
class CoroutineSampleApplication : Application<CoroutineSampleConfiguration>() {

    override fun run(
        configuration: CoroutineSampleConfiguration,
        environment: Environment
    ) {
        // 1. Initialize repositories
        val participantRepository = ParticipantRepository()
        val contributionRepository = ContributionRepository()
        val fundReturnRateRepository = FundReturnRateRepository()
        val pensionRulesRepository = PensionRulesRepository()

        // 2. Initialize services (inject repositories)
        val simulationService = BenefitSimulationService(
            participantRepository = participantRepository,
            contributionRepository = contributionRepository,
            fundReturnRateRepository = fundReturnRateRepository,
            pensionRulesRepository = pensionRulesRepository,
            timeoutMillis = configuration.getCoroutineConfig().timeoutMillis
        )

        // 3. Register REST resources
        environment.jersey().register(BenefitSimulationResource(simulationService))
        environment.jersey().register(HealthCheckResource())
    }
}

fun main(args: Array<String>) {
    CoroutineSampleApplication().run(*args)
}
```

**Dependency Flow:**
```
CoroutineSampleApplication
    ↓ creates
Repositories (ParticipantRepository, ContributionRepository, etc.)
    ↓ injected into
BenefitSimulationService
    ↓ injected into
BenefitSimulationResource (REST API)
    ↓ registered in
DropWizard Environment
```

---

## 💡 Understanding Coroutines

### Key Concepts

#### 1. `suspend` Keyword

```kotlin
suspend fun fetchData(): String {
    delay(1000)
    return "data"
}
```

- Marks a function as suspendable
- Can be paused and resumed
- Doesn't block the thread while waiting

#### 2. `coroutineScope { }`

```kotlin
coroutineScope {
    // Structured concurrency
    // All child coroutines must complete
}
```

- Creates a scope for coroutines
- If any child fails, all are cancelled
- Waits for all children to complete

#### 3. `async { }` and `await()`

```kotlin
val deferred = async { fetchData() }  // Start immediately
val result = deferred.await()          // Wait for result
```

- `async` launches a coroutine that returns a value
- `await()` suspends until result is ready
- Perfect for parallel execution

#### 4. `runBlocking { }`

```kotlin
fun main() = runBlocking {
    // Bridge to coroutine world
    val result = suspendFunction()
}
```

- Blocks the current thread
- Used to bridge blocking and non-blocking code
- Use sparingly (mainly in main functions and tests)

### Coroutine vs Thread

| Feature | Thread | Coroutine |
|---------|--------|-----------|
| Weight | Heavy (1-2 MB) | Lightweight (few KB) |
| Context Switch | Expensive (OS level) | Cheap (library level) |
| Blocking | Blocks thread | Suspends, doesn't block |
| Number | Thousands | Millions possible |

---

## 🧪 Testing the Application

### Step 1: Build the Project

```bash
./gradlew clean build
```

### Step 2: Run the Application

```bash
java -jar build/libs/coroutine-sample-1.0.0.jar server config.yml
```

Or if you don't have config.yml:

```bash
./gradlew run --args="server config.yml"
```

### Step 3: Test the Endpoints

#### Test Single Simulation

```bash
curl -X GET http://localhost:8080/api/simulations/P001
```

**Expected Response:**
```json
{
  "participantId": "P001",
  "participantName": "Budi Santoso",
  "currentAge": 45,
  "yearsOfService": 19,
  "totalContributions": 45000000.0,
  "projectedFundValue": 48600000.0,
  "estimatedLumpSum": 38880000.0,
  "estimatedMonthlyBenefit": 216000.0,
  "isEligibleForRetirement": false,
  "earlyRetirementPenalty": 0.4,
  "simulationTimestamp": "2026-02-04T13:45:30",
  "calculationDurationMs": 450,
  "details": {
    "appliedReturnRate": 0.08,
    "monthsOfContribution": 228,
    "retirementAge": 58,
    "minimumYearsOfService": 5,
    "calculationMethod": "COMPOUND_INTEREST_WITH_PENALTY"
  }
}
```

#### Test Batch Simulation

```bash
curl -X POST http://localhost:8080/api/simulations/batch \
  -H "Content-Type: application/json" \
  -d '{"participantIds": ["P001", "P002", "P003"]}'
```

#### Test Health Check

```bash
curl -X GET http://localhost:8080/api/health
```

---

## 🎓 Key Takeaways

### 1. When to Use Coroutines?

✅ **Use Coroutines When:**
- You have multiple independent I/O operations
- You need to fetch data from multiple sources
- You want to improve response time
- You're doing read-heavy operations

❌ **Don't Use Coroutines When:**
- You have sequential dependencies (A needs B needs C)
- You're doing CPU-intensive calculations
- Single simple operation

### 2. Coroutine Best Practices

1. **Always use structured concurrency** (`coroutineScope`)
2. **Handle timeouts** (`withTimeout`)
3. **Use `async/await` for parallel operations**
4. **Proper error handling** (try-catch in coroutine scope)
5. **Use `runBlocking` only at boundaries** (main, tests, REST endpoints)

### 3. Performance Benefits

**This Project:**
- Sequential: ~630ms
- With Coroutines: ~450ms
- **Improvement: 28% faster**

**Real-World Scenarios:**
- More data sources = bigger improvement
- Network latency = more benefits
- Can handle 10x more concurrent requests

### 4. Code Organization

```
Domain Models → Repositories → Services → REST API
     ↓              ↓              ↓          ↓
  Data DTOs    Data Access    Business    HTTP
                              Logic     Endpoints
                            (Coroutines
                             work here!)
```

---

## 📚 Further Learning

### Next Steps

1. **Add Real Database** - Replace in-memory data with PostgreSQL/MySQL
2. **Add Caching** - Cache fund rates and rules (they change rarely)
3. **Add Metrics** - Track performance with Micrometer
4. **Add Tests** - Unit tests with `runTest` and `TestDispatcher`
5. **Add Authentication** - Secure the API with JWT

### Recommended Reading

- [Kotlin Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [DropWizard Documentation](https://www.dropwizard.io/)
- [Structured Concurrency](https://kotlinlang.org/docs/coroutines-basics.html#structured-concurrency)

---

## 🎯 Summary

**Problem:** Calculate pension benefits by fetching data from 4 sources (slow if sequential)

**Solution:** Use Kotlin Coroutines to fetch data in parallel

**Key File:** `BenefitSimulationService.kt` - Where the magic happens!

**Result:** 28% faster response time with clean, readable code

**Architecture:**
```
REST API → Service (with coroutines) → Repositories → Data
```

**Coroutine Flow:**
```
Fetch participant → Launch 3 async tasks → await all → calculate → return
```

---

## 📞 Questions?

If you're stuck, check:
1. `BenefitSimulationService.kt:60` - Main coroutine logic
2. `BenefitSimulationResource.kt:35` - REST endpoint with runBlocking
3. `CoroutineSampleApplication.kt:40` - Wiring dependencies

Happy Learning! 🚀
