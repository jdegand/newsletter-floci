# Newsletter Floci

A lightweight demo showing:

- Angular frontend (newsletter form)

- Spring Boot backend (subscription API)

- DynamoDB (stores subscribers)

- SES (sends confirmation email)

- Floci (local AWS emulator)

Everything runs locally with no AWS account required.

## How to Use

1. Start Floci

    ```bash
    sudo docker compose up -d
    ```

    Floci exposes AWS services at: `http://localhost:4566`

1. Start the Spring Boot API

    ```bash
    mvn spring-boot:run
    ```

    Or run it from your IDE.

    API runs at: `http://localhost:8080`

1. Start the Angular Frontend

    ```bash
    cd newsletter-frontend
    npm start
    ```

    Frontend runs at: `http://localhost:4200`

    ---

1. Create the DynamoDB table

    You need the AWS CLI installed locally:

    ```bash
    sudo apt install awscli
    ```

    Configure your credentials:

    ```bash
    aws configure
    ```

    Run this **once**:

    ```bash
    aws --endpoint-url=http://localhost:4566 dynamodb create-table \
      --table-name Subscribers \
      --attribute-definitions AttributeName=email,AttributeType=S \
      --key-schema AttributeName=email,KeyType=HASH \
      --billing-mode PAY_PER_REQUEST
    ```

    Verify:

    ```bash
    aws --endpoint-url=http://localhost:4566 dynamodb list-tables
    ```

    Expected:

    ```json
    {
      "TableNames": ["Subscribers"]
    }
    ```

    ---

1. Verify SES identity

    Floci requires SES identities to be verified before sending.

    Run:

    ```bash
    aws --endpoint-url=http://localhost:4566 ses verify-email-identity \
      --email-address no-reply@example.com
    ```

    Check:

    ```bash
    aws --endpoint-url=http://localhost:4566 ses list-identities
    ```

    Expected:

    ```json
    {
      "Identities": ["no-reply@example.com"]
    }
    ```

    ---

1. Test the Newsletter Flow

      - Open `http://localhost:4200`

      - Enter an email (e.g., `test@example.com`)

      - Click **Subscribe**.

      What happens:

      - Angular → sends POST to Spring Boot.

      - Spring Boot → saves subscriber to DynamoDB.

      - Spring Boot → sends SES email.

      - Floci → captures the SES email.

      ---

1. Check DynamoDB

    Run:

    ```bash
    aws --endpoint-url=http://localhost:4566 dynamodb scan --table-name Subscribers
    ```

    Example output:

    ```json
    {
      "Items": [
        {
          "email": { "S": "test@example.com" },
          "subscribedAt": { "S": "2025-05-03T19:12:34Z" }
        }
      ],
      "Count": 1,
      "ScannedCount": 1
    }
    ```

    ---

1. Check the Email (Floci SES Inbox)

    Floci stores all SES emails here:

    ```bash
    http://localhost:4566/_aws/ses
    ```

    You’ll see the email and metadata.

    ---

## Notes

- Floci’s SES implementation differs slightly from LocalStack and evolves quickly.

- SMTP relay to Mailpit didn’t work reliably, so Mailpit was removed.

- Using Floci’s built‑in SES inbox (`/_aws/ses`) is the simplest and most stable way to inspect emails locally.
