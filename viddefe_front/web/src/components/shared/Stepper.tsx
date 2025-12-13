interface StepperProps {
  steps: string[];
  currentStep: number;
}

export default function Stepper({ steps, currentStep }: StepperProps) {
  return (
    <div className="mb-8">
      <div className="flex items-center justify-between">
        {steps.map((step, idx) => (
          <div key={idx} className="flex items-center flex-1">
            {/* Step circle */}
            <div
              className={`w-10 h-10 rounded-full flex items-center justify-center font-semibold text-sm transition-all
                ${
                  idx < currentStep
                    ? 'bg-green-500 text-white'
                    : idx === currentStep
                    ? 'bg-primary-600 text-white'
                    : 'bg-gray-200 text-gray-700'
                }
              `}
            >
              {idx < currentStep ? '✓' : idx + 1}
            </div>

            {/* Step label */}
            <div className="ml-3">
              <p
                className={`text-sm font-medium ${
                  idx <= currentStep ? 'text-primary-800' : 'text-gray-600'
                }`}
              >
                {step}
              </p>
            </div>

            {/* Connector line */}
            {idx < steps.length - 1 && (
              <div
                className={`flex-1 h-1 mx-4 ${
                  idx < currentStep ? 'bg-green-500' : 'bg-gray-200'
                }`}
              />
            )}
          </div>
        ))}
      </div>
    </div>
  );
}
