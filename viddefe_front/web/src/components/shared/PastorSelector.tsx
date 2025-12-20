import { usePeople } from '../../hooks';
import DropDown from './DropDown';
import Avatar from './Avatar';

interface PastorSelectorProps {
  label?: string;
  value?: string;
  onChangeValue?: (value: string) => void;
  error?: string;
  className?: string;
  placeholder?: string;
}

export default function PastorSelector({
  label = 'Pastor',
  value,
  onChangeValue,
  error,
  className = '',
  placeholder = 'Seleccionar pastor...',
}: PastorSelectorProps) {
  const { data: people } = usePeople();
  
  const peopleList = Array.isArray(people) ? people : (people?.content ?? []);
  
  const options = peopleList.map((person) => ({
    value: person.id,
    label: `${person.firstName} ${person.lastName}`,
    avatar: (person as any).avatar,
  }));

  const selectedPerson = peopleList.find((p) => p.id === value);

  return (
    <div className={className}>
      {selectedPerson && (
        <div className="flex items-center gap-2 mb-2 p-2 bg-primary-50 rounded-lg">
          <Avatar
            src={(selectedPerson as any).avatar}
            name={`${selectedPerson.firstName} ${selectedPerson.lastName}`}
            size="sm"
          />
          <span className="text-sm text-primary-800 font-medium">
            {selectedPerson.firstName} {selectedPerson.lastName}
          </span>
        </div>
      )}
      <DropDown
        label={label}
        options={options}
        value={value || ''}
        onChangeValue={onChangeValue}
        placeholder={placeholder}
        error={error}
        searchKey="label"
      />
    </div>
  );
}
