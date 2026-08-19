# Seeds real Kenya property data through the live REST API (gateway on :8085),
# authenticating against the real Keycloak realm imported into hfdev-keycloak.
# Run only after: docker compose up -d, app (port 9090) and gateway-service
# (port 8085) are both healthy.

$ErrorActionPreference = "Stop"
$KC = "http://localhost:8081/realms/hostflow/protocol/openid-connect/token"
$API = "http://localhost:9090/api/v1"

function Get-Token($username, $password, $clientId, $clientSecret) {
    $body = @{
        grant_type    = "password"
        client_id     = $clientId
        client_secret = $clientSecret
        username      = $username
        password      = $password
        scope         = "openid"
    }
    $resp = Invoke-RestMethod -Uri $KC -Method Post -Body $body -ContentType "application/x-www-form-urlencoded"
    return $resp.access_token
}

function Auth-Headers($token) {
    return @{ Authorization = "Bearer $token" }
}

Write-Output "== Authenticating as platformadmin =="
$adminToken = Get-Token "platformadmin" "DevPass123!" "hostflow-web" "dev-secret-hostflow-web"

Write-Output "== Onboarding organization =="
if ($env:SKIP_ORG_ONBOARDING -eq "1") {
    Write-Output "Skipping (already provisioned in a prior run)."
} else {
    $orgBody = @{
        organizationName = "NazilCo Kenya Properties"
        organizationSlug = "nazilco-kenya"
        primaryProduct   = "XANUOS"
        adminFirstName   = "Test"
        adminLastName    = "Owner"
        adminEmail       = "owner@hostflow.dev"
    } | ConvertTo-Json

    try {
        $orgResp = Invoke-RestMethod -Uri "$API/organizations" -Method Post -Body $orgBody -ContentType "application/json" -Headers (Auth-Headers $adminToken)
        $orgId = $orgResp.data.id
        Write-Output "Organization created: $orgId"
    } catch {
        Write-Output "Org onboarding failed:"
        Write-Output $_.ErrorDetails.Message
        throw
    }
}

Write-Output "== Getting owner token =="
$ownerToken = Get-Token "owner@hostflow.dev" "DevPass123!" "hostflow-web" "dev-secret-hostflow-web"
$ownerHeaders = Auth-Headers $ownerToken

# ---- Property definitions (matches nazilco-web's Kenya demo data) ----
$properties = @(
    @{
        name = "Azure Cliffside Villa"; propertyType = "VACATION_RENTAL"
        addressLine = "14 Marina Point Road"; city = "Santorini"; country = "Greece"
        description = "Perched above the coastline with uninterrupted ocean views, this villa pairs sun-bleached stone with floor-to-ceiling glass."
        basePrice = "54000.00"; latitude = 36.3932; longitude = 25.4615
        photos = @("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80")
    },
    @{
        name = "Maasai Mara Safari Camp"; propertyType = "VACATION_RENTAL"
        addressLine = "Mara Triangle Conservancy Road"; city = "Narok"; country = "Kenya"
        description = "A luxury tented camp on the edge of the Maasai Mara, where the sound of the savanna replaces any alarm clock."
        basePrice = "42000.00"; latitude = -1.4061; longitude = 35.0147
        photos = @("https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1523805009345-7448845a9e53?auto=format&fit=crop&w=1200&q=80")
    },
    @{
        name = "Kilimani Sky Residence"; propertyType = "RESIDENTIAL"
        addressLine = "12 Wood Avenue, Kilimani"; city = "Nairobi"; country = "Kenya"
        description = "A designer apartment on the 18th floor overlooking Nairobi's skyline and the Ngong Hills beyond."
        basePrice = "13500.00"; latitude = -1.2954; longitude = 36.7870
        photos = @("https://images.unsplash.com/photo-1560185893-a55cbc8c57e8?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?auto=format&fit=crop&w=1200&q=80")
    },
    @{
        name = "Casa de Bugambilia"; propertyType = "VACATION_RENTAL"
        addressLine = "22 Calle de las Flores"; city = "Oaxaca"; country = "Mexico"
        description = "A hacienda-style courtyard home draped in bougainvillea, with hand-painted tile and a plunge pool."
        basePrice = "23000.00"; latitude = 17.0732; longitude = -96.7266
        photos = @("https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=1200&q=80")
    },
    @{
        name = "Diani Beach Villa"; propertyType = "VACATION_RENTAL"
        addressLine = "7 Beach Road, Diani"; city = "Diani Beach"; country = "Kenya"
        description = "Barefoot luxury steps from the white sand of Diani, with a rooftop deck built for long sundowners."
        basePrice = "28500.00"; latitude = -4.2761; longitude = 39.5908
        photos = @("https://images.unsplash.com/photo-1519046904884-53103b34b206?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=1200&q=80")
    },
    @{
        name = "Kyoto Machiya House"; propertyType = "VACATION_RENTAL"
        addressLine = "3 Higashiyama Lane"; city = "Kyoto"; country = "Japan"
        description = "A restored 1920s machiya townhouse with a private tsubo-niwa garden and a modern cedar soaking tub."
        basePrice = "31500.00"; latitude = 35.0116; longitude = 135.7681
        photos = @("https://images.unsplash.com/photo-1600664356215-9c611f5b4d80?auto=format&fit=crop&w=1200&q=80")
    },
    @{
        name = "The Acacia Nairobi Hotel"; propertyType = "HOTEL"
        addressLine = "45 Waiyaki Way, Westlands"; city = "Nairobi"; country = "Kenya"
        description = "A boutique hotel in the heart of Westlands, built around a courtyard of acacia trees."
        basePrice = "18500.00"; latitude = -1.2667; longitude = 36.8038
        photos = @("https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=1200&q=80")
    }
)

$createdIds = @()

foreach ($p in $properties) {
    Write-Output "== Creating property: $($p.name) =="
    $createBody = @{
        name = $p.name; propertyType = $p.propertyType
        addressLine = $p.addressLine; city = $p.city; country = $p.country
    } | ConvertTo-Json
    $created = Invoke-RestMethod -Uri "$API/properties" -Method Post -Body $createBody -ContentType "application/json" -Headers $ownerHeaders
    $id = $created.data.id
    $createdIds += $id

    $detailsBody = @{
        description = $p.description; basePrice = $p.basePrice
        latitude = $p.latitude; longitude = $p.longitude
    } | ConvertTo-Json
    Invoke-RestMethod -Uri "$API/properties/$id" -Method Patch -Body $detailsBody -ContentType "application/json" -Headers $ownerHeaders | Out-Null

    $photoIndex = 0
    foreach ($photoUrl in $p.photos) {
        $photoIndex++
        $tmpFile = Join-Path $env:TEMP "seed-photo-$photoIndex.jpg"
        try {
            Invoke-WebRequest -Uri $photoUrl -OutFile $tmpFile -UseBasicParsing
            $form = @{
                file = Get-Item -Path $tmpFile
                documentType = "PHOTO"
            }
            Invoke-RestMethod -Uri "$API/properties/$id/documents" -Method Post -Form $form -Headers $ownerHeaders | Out-Null
        } catch {
            Write-Output "  photo upload failed for $($p.name): $($_.Exception.Message)"
        } finally {
            Remove-Item $tmpFile -ErrorAction SilentlyContinue
        }
    }

    Invoke-RestMethod -Uri "$API/properties/$id/publish" -Method Patch -Headers $ownerHeaders | Out-Null
    Write-Output "  published: $id"
}

Write-Output "== Done. Created $($createdIds.Count) properties. =="
Write-Output "Verify: curl http://localhost:8085/api/v1/properties/public"
