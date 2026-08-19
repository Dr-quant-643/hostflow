# One-off: upload photos for properties already created by seed-kenya-data.ps1
# (their photo upload failed under legacy Windows PowerShell, which lacks -Form).
# Run with pwsh (PowerShell 7), not powershell.exe.

$ErrorActionPreference = "Stop"
$KC = "http://localhost:8081/realms/hostflow/protocol/openid-connect/token"
$API = "http://localhost:9090/api/v1"

$body = @{
    grant_type    = "password"
    client_id     = "hostflow-web"
    client_secret = "dev-secret-hostflow-web"
    username      = "owner@hostflow.dev"
    password      = "DevPass123!"
    scope         = "openid"
}
$ownerToken = (Invoke-RestMethod -Uri $KC -Method Post -Body $body -ContentType "application/x-www-form-urlencoded").access_token
$ownerHeaders = @{ Authorization = "Bearer $ownerToken" }

$propertyPhotos = @{
    "7204e092-387a-42f1-adeb-3b805d67a042" = @("https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1600585154340-be6161a56a0c?auto=format&fit=crop&w=1200&q=80")
    "eadb4a2d-45d4-4f72-92ff-e8ca2dd79685" = @("https://images.unsplash.com/photo-1516426122078-c23e76319801?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1523805009345-7448845a9e53?auto=format&fit=crop&w=1200&q=80")
    "6cd7d019-243a-443a-8a64-84fecc64c2bb" = @("https://images.unsplash.com/photo-1560185893-a55cbc8c57e8?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1502005229762-cf1b2da7c5d6?auto=format&fit=crop&w=1200&q=80")
    "f84d5a78-0ec8-4fc5-9597-885302cb8fdc" = @("https://images.unsplash.com/photo-1580587771525-78b9dba3b914?auto=format&fit=crop&w=1200&q=80")
    "9468b5ad-b18d-43e7-992d-7bcd5b4677a9" = @("https://images.unsplash.com/photo-1519046904884-53103b34b206?auto=format&fit=crop&w=1200&q=80","https://images.unsplash.com/photo-1544551763-46a013bb70d5?auto=format&fit=crop&w=1200&q=80")
    "392915a4-24db-4028-ae89-be4449ca0e59" = @("https://images.unsplash.com/photo-1600664356215-9c611f5b4d80?auto=format&fit=crop&w=1200&q=80")
    "1d036725-4568-47bb-b54c-a6cc255f035d" = @("https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=1200&q=80")
}

foreach ($id in $propertyPhotos.Keys) {
    Write-Output "== Uploading photos for $id =="
    $photoIndex = 0
    foreach ($photoUrl in $propertyPhotos[$id]) {
        $photoIndex++
        $tmpFile = Join-Path $env:TEMP "seed-photo-$id-$photoIndex.jpg"
        try {
            Invoke-WebRequest -Uri $photoUrl -OutFile $tmpFile -UseBasicParsing
            $form = @{
                file = Get-Item -Path $tmpFile
                documentType = "PHOTO"
            }
            Invoke-RestMethod -Uri "$API/properties/$id/documents" -Method Post -Form $form -Headers $ownerHeaders | Out-Null
            Write-Output "  uploaded photo $photoIndex"
        } catch {
            Write-Output "  photo upload failed: $($_.Exception.Message)"
            Write-Output "  $($_.ErrorDetails.Message)"
        } finally {
            Remove-Item $tmpFile -ErrorAction SilentlyContinue
        }
    }
}
Write-Output "== Done =="
